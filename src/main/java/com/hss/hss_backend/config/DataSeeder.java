package com.hss.hss_backend.config;

import com.hss.hss_backend.entity.*;
import com.hss.hss_backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Data seeder for local development and testing
 * Seeds database with sample data when application starts
 */
@Component
@Profile({"local", "dev"})
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;
    private final OwnerRepository ownerRepository;
    private final AnimalRepository animalRepository;
    private final StaffRepository staffRepository;
    private final StaffRoleRepository staffRoleRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🌱 Starting data seeding...");

        // Check if data already exists
        if (speciesRepository.count() > 0) {
            log.info("📊 Database already contains data, skipping seed");
            return;
        }

        try {
            seedRoles();
            seedSpeciesAndBreeds();
            seedOwners();
            seedAnimals();
            seedStaff();
            
            log.info("✅ Data seeding completed successfully!");
        } catch (Exception e) {
            log.error("❌ Error during data seeding", e);
        }
    }

    private void seedRoles() {
        log.info("Seeding roles...");
        
        List<String> roleNames = Arrays.asList("ADMIN", "VETERINARIAN", "STAFF", "RECEPTIONIST");
        
        for (String roleName : roleNames) {
            if (!roleRepository.findByName(roleName).isPresent()) {
                Role role = new Role();
                role.setName(roleName);
                role.setDescription(roleName + " role");
                roleRepository.save(role);
            }
        }
    }

    private void seedSpeciesAndBreeds() {
        log.info("Seeding species and breeds...");

        // Kedi
        Species kedi = new Species();
        kedi.setName("Kedi");
        kedi = speciesRepository.save(kedi);

        createBreed(kedi, "Tekir");
        createBreed(kedi, "Persian");
        createBreed(kedi, "British Shorthair");
        createBreed(kedi, "Scottish Fold");

        // Köpek
        Species kopek = new Species();
        kopek.setName("Köpek");
        kopek = speciesRepository.save(kopek);

        createBreed(kopek, "Golden Retriever");
        createBreed(kopek, "Labrador");
        createBreed(kopek, "German Shepherd");
        createBreed(kopek, "Bulldog");

        // Kuş
        Species kus = new Species();
        kus.setName("Kuş");
        kus = speciesRepository.save(kus);

        createBreed(kus, "Muhabbet Kuşu");
        createBreed(kus, "Kanarya");
        createBreed(kus, "Papağan");
    }

    private void createBreed(Species species, String name) {
        Breed breed = new Breed();
        breed.setName(name);
        breed.setSpecies(species);
        breedRepository.save(breed);
    }

    private void seedOwners() {
        log.info("Seeding owners...");

        Owner owner1 = new Owner();
        owner1.setFirstName("Ahmet");
        owner1.setLastName("Yılmaz");
        owner1.setEmail("ahmet.yilmaz@example.com");
        owner1.setPhone("5551112233");
        owner1.setAddress("İstanbul, Kadıköy");
        ownerRepository.save(owner1);

        Owner owner2 = new Owner();
        owner2.setFirstName("Ayşe");
        owner2.setLastName("Kaya");
        owner2.setEmail("ayse.kaya@example.com");
        owner2.setPhone("5552223344");
        owner2.setAddress("Ankara, Çankaya");
        ownerRepository.save(owner2);

        Owner owner3 = new Owner();
        owner3.setFirstName("Mehmet");
        owner3.setLastName("Demir");
        owner3.setEmail("mehmet.demir@example.com");
        owner3.setPhone("5553334455");
        owner3.setAddress("İzmir, Konak");
        ownerRepository.save(owner3);
    }

    private void seedAnimals() {
        log.info("Seeding animals...");

        List<Owner> owners = ownerRepository.findAll();
        List<Species> species = speciesRepository.findAll();
        List<Breed> breeds = breedRepository.findAll();

        if (owners.isEmpty() || species.isEmpty() || breeds.isEmpty()) {
            log.warn("Cannot seed animals: missing owners, species, or breeds");
            return;
        }

        // Create animals for each owner
        for (int i = 0; i < owners.size(); i++) {
            Owner owner = owners.get(i);
            Species speciesForAnimal = species.get(i % species.size());
            List<Breed> breedsForSpecies = breedRepository.findBySpeciesSpeciesId(speciesForAnimal.getSpeciesId());
            
            if (breedsForSpecies.isEmpty()) {
                continue;
            }
            
            Breed breed = breedsForSpecies.get(0);

            Animal animal = new Animal();
            animal.setName("Test Hayvan " + (i + 1));
            animal.setOwner(owner);
            animal.setSpecies(speciesForAnimal);
            animal.setBreed(breed);
            animal.setGender(Animal.Gender.values()[i % Animal.Gender.values().length]);
            animal.setBirthDate(LocalDate.now().minusYears(2 + i));
            animal.setWeight(java.math.BigDecimal.valueOf(5.0 + i * 2));
            animal.setMicrochipNo("12345678901234" + i);
            animal.setAllergies(i % 2 == 0 ? "Pollen" : null);
            animal.setChronicDiseases(i % 3 == 0 ? "Diabetes" : null);
            
            animalRepository.save(animal);
        }

        log.info("Seeded {} animals", animalRepository.count());
    }

    private void seedStaff() {
        log.info("Seeding staff...");

        List<Role> roles = roleRepository.findAll();
        if (roles.isEmpty()) {
            log.warn("Cannot seed staff: no roles found");
            return;
        }

        Role vetRole = roles.stream()
                .filter(r -> r.getName().equals("VETERINARIAN"))
                .findFirst()
                .orElse(roles.get(0));
        
        Role receptionistRole = roles.stream()
                .filter(r -> r.getName().equals("RECEPTIONIST"))
                .findFirst()
                .orElse(roles.get(0));

        Staff staff1 = new Staff();
        staff1.setFullName("Dr. Ali Veteriner");
        staff1.setEmail("ali.veteriner@hss.com");
        staff1.setPhone("5554445566");
        staff1.setHireDate(LocalDate.now().minusYears(2));
        staff1.setActive(true);
        staff1.setDepartment("Veterinerlik");
        staff1.setPosition("Veteriner Hekim");
        staff1 = staffRepository.save(staff1);

        // Assign role to staff
        StaffRole staffRole1 = new StaffRole();
        staffRole1.setStaff(staff1);
        staffRole1.setRole(vetRole);
        staffRole1.setAssignedDate(LocalDate.now());
        staffRoleRepository.save(staffRole1);

        Staff staff2 = new Staff();
        staff2.setFullName("Ayşe Sekreter");
        staff2.setEmail("ayse.sekreter@hss.com");
        staff2.setPhone("5555556677");
        staff2.setHireDate(LocalDate.now().minusYears(1));
        staff2.setActive(true);
        staff2.setDepartment("Yönetim");
        staff2.setPosition("Sekreter");
        staff2 = staffRepository.save(staff2);

        // Assign role to staff
        StaffRole staffRole2 = new StaffRole();
        staffRole2.setStaff(staff2);
        staffRole2.setRole(receptionistRole);
        staffRole2.setAssignedDate(LocalDate.now());
        staffRoleRepository.save(staffRole2);
    }
}

