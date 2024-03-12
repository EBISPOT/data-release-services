package uk.ac.ebi.spot.gwas.data.copy.table.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.data.copy.table.model.UnpublishedAncestry;

public interface UnpublishedAncestryRepository extends JpaRepository<UnpublishedAncestry, Long> {}
