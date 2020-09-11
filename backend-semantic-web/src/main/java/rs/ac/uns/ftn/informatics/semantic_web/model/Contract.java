package rs.ac.uns.ftn.informatics.semantic_web.model;

import java.util.Date;

import javax.persistence.Column;
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
	
	@Column(nullable = false)
	private String address;
	
	@Column(nullable = false)
	private String status;
	
	@Column(nullable = false)
	private Date startDate;
	
	@Column(nullable = false)
	private Date endDate;
	
	@ManyToOne
	private Agency agency;
	
	@ManyToOne
	private Accomodation accomodation;
	
	// @Column(columnDefinition = "boolean default false", nullable = false)
	// private Boolean seen;
	
	private Long agReprId;
	private Long accReprId;
}
