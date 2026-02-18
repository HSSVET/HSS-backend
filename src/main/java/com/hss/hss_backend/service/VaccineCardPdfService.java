package com.hss.hss_backend.service;

import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.VaccinationRecord;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.repository.AnimalRepository;
import com.hss.hss_backend.repository.VaccinationRecordRepository;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VaccineCardPdfService {

    private final AnimalRepository animalRepository;
    private final VaccinationRecordRepository vaccinationRecordRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public byte[] generateVaccineCard(Long animalId) {
        log.info("Generating vaccine card PDF for animal ID: {}", animalId);

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", animalId));

        List<VaccinationRecord> vaccinations = vaccinationRecordRepository
                .findByAnimal_AnimalIdOrderByDateDesc(animalId);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            DeviceRgb primaryColor = new DeviceRgb(146, 167, 140);
            DeviceRgb secondaryColor = new DeviceRgb(247, 205, 130);

            document.add(new Paragraph("AŞI KARNESİ")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(primaryColor)
                    .setMarginBottom(20));

            Table animalInfoTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .useAllAvailableWidth();

            animalInfoTable.addCell(createHeaderCell("Hayvan Bilgileri"));
            animalInfoTable.addCell(new Cell());

            addInfoRow(animalInfoTable, "İsim:", animal.getName());
            addInfoRow(animalInfoTable, "Tür:", animal.getSpecies() != null ? animal.getSpecies().getName() : "-");
            addInfoRow(animalInfoTable, "Irk:", animal.getBreed() != null ? animal.getBreed().getName() : "-");
            addInfoRow(animalInfoTable, "Mikroçip No:", animal.getMicrochipNo() != null ? animal.getMicrochipNo() : "-");
            addInfoRow(animalInfoTable, "Doğum Tarihi:",
                    animal.getBirthDate() != null ? animal.getBirthDate().format(DATE_FORMATTER) : "-");

            document.add(animalInfoTable.setMarginBottom(10));

            if (animal.getOwner() != null) {
                Table ownerInfoTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                        .useAllAvailableWidth();

                ownerInfoTable.addCell(createHeaderCell("Sahip Bilgileri"));
                ownerInfoTable.addCell(new Cell());

                String ownerName = (animal.getOwner().getFirstName() != null ? animal.getOwner().getFirstName() : "")
                        + " " + (animal.getOwner().getLastName() != null ? animal.getOwner().getLastName() : "");
                addInfoRow(ownerInfoTable, "Ad Soyad:", ownerName.trim());
                addInfoRow(ownerInfoTable, "Telefon:",
                        animal.getOwner().getPhone() != null ? animal.getOwner().getPhone() : "-");

                document.add(ownerInfoTable.setMarginBottom(20));
            }

            document.add(new Paragraph("Aşı Kayıtları")
                    .setFontSize(14)
                    .setBold()
                    .setFontColor(primaryColor)
                    .setMarginBottom(10));

            if (vaccinations.isEmpty()) {
                document.add(new Paragraph("Henüz aşı kaydı bulunmamaktadır.")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setItalic());
            } else {
                Table vaccineTable = new Table(UnitValue.createPercentArray(
                        new float[]{2, 2, 1.5f, 1.5f, 1.5f}))
                        .useAllAvailableWidth();

                vaccineTable.addHeaderCell(createHeaderCell("Aşı Adı"));
                vaccineTable.addHeaderCell(createHeaderCell("Tarih"));
                vaccineTable.addHeaderCell(createHeaderCell("Sonraki Tarih"));
                vaccineTable.addHeaderCell(createHeaderCell("Seri No"));
                vaccineTable.addHeaderCell(createHeaderCell("Veteriner"));

                for (VaccinationRecord vaccination : vaccinations) {
                    vaccineTable.addCell(new Cell().add(new Paragraph(vaccination.getVaccineName())));
                    vaccineTable.addCell(new Cell().add(new Paragraph(
                            vaccination.getDate() != null ? vaccination.getDate().format(DATE_FORMATTER) : "-")));
                    vaccineTable.addCell(new Cell().add(new Paragraph(
                            vaccination.getNextDueDate() != null ? vaccination.getNextDueDate().format(DATE_FORMATTER) : "-")));
                    vaccineTable.addCell(new Cell().add(new Paragraph(
                            vaccination.getSerialNumber() != null ? vaccination.getSerialNumber() : "-")));
                    vaccineTable.addCell(new Cell().add(new Paragraph(
                            vaccination.getVeterinarianName() != null ? vaccination.getVeterinarianName() : "-")));
                }

                document.add(vaccineTable);
            }

            document.add(new Paragraph("\n\nBu belge " + java.time.LocalDate.now().format(DATE_FORMATTER) +
                    " tarihinde elektronik olarak oluşturulmuştur.")
                    .setFontSize(8)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20));

            if (animal.getClinic() != null && animal.getClinic().getName() != null) {
                document.add(new Paragraph(animal.getClinic().getName())
                        .setFontSize(10)
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontColor(primaryColor));
            }

            document.close();

            log.info("Vaccine card PDF generated successfully for animal ID: {}", animalId);
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generating vaccine card PDF for animal ID: {}", animalId, e);
            throw new RuntimeException("Failed to generate vaccine card PDF", e);
        }
    }

    private Cell createHeaderCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold())
                .setBackgroundColor(new DeviceRgb(146, 167, 140))
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private void addInfoRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setBold()));
        table.addCell(new Cell().add(new Paragraph(value != null ? value : "-")));
    }
}
