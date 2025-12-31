package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.VaccinationProtocol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccinationProtocolRepository extends JpaRepository<VaccinationProtocol, Long> {

    List<VaccinationProtocol> findBySpeciesSpeciesId(Long speciesId);

    List<VaccinationProtocol> findByVaccineVaccineId(Long vaccineId);

    List<VaccinationProtocol> findByIsActive(Boolean isActive);

    @Query("SELECT vp FROM VaccinationProtocol vp WHERE vp.species.speciesId = :speciesId AND vp.isActive = true")
    List<VaccinationProtocol> findActiveProtocolsBySpeciesId(@Param("speciesId") Long speciesId);

    @Query("SELECT vp FROM VaccinationProtocol vp WHERE vp.species.speciesId = :speciesId AND vp.vaccine.vaccineId = :vaccineId AND vp.isActive = true")
    List<VaccinationProtocol> findActiveProtocolsBySpeciesAndVaccine(@Param("speciesId") Long speciesId, 
                                                                     @Param("vaccineId") Long vaccineId);
}

