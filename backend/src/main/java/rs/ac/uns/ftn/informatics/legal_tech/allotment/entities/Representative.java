package rs.ac.uns.ftn.informatics.legal_tech.allotment.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
public @Data class Representative {
	
	@Id
	private String networkAddress;
	
	private String fullName;
	private String displayName;
	private String password;
	private String email;
	private String phoneNumber;
	
	@ManyToOne
	private Organization representing;

}
