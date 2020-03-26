package rs.ac.uns.ftn.informatics.legal_tech.allotment.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public @Data class Organization {
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	protected Long id;
	
	protected String name;
	protected String country;
	protected String city;
	protected String address;
	protected String account;

}
