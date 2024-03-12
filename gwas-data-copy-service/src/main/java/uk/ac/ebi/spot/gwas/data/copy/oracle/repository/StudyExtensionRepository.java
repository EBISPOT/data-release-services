package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.ac.ebi.spot.gwas.data.copy.model.StudyExtension;


public interface StudyExtensionRepository extends JpaRepository<StudyExtension, Long> {
    @Query(value = "SELECT SE.ID FROM STUDY_EXTENSION SE WHERE SE.STUDY_ID= :studyId", nativeQuery = true) StudyExtension getStudyExtensionId(Long studyId);
}
