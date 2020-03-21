package rs.ac.uns.ftn.informatics.legal_tech.allotment.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
public @Data class Contract {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String address;
	private String status;
	
	@ManyToOne
	private Agency agency;
	
	@ManyToOne
	private Organization org;
	
}
