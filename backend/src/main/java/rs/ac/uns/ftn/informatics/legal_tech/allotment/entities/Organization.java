package rs.ac.uns.ftn.informatics.legal_tech.allotment.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public @Data class Organization {
	
	@Id
	protected String networkAddress;
	
	protected String name;
	protected String country;
	protected String city;
	protected String address;

}
