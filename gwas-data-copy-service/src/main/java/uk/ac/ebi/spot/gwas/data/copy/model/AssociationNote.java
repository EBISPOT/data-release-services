package uk.ac.ebi.spot.gwas.data.copy.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by cinzia on 28/03/2017.
 */
@Entity
@DiscriminatorValue(value = "Association")
public class AssociationNote extends Note {
}
