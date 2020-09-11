package rs.ac.uns.ftn.informatics.semantic_web.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
public @Data class Representative {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String fullName;
	private String displayName;
	private String password;
	private String email;
	private String phoneNumber;
	// private Long account_id;
	
	@ManyToOne
	private Organization representing;

}
