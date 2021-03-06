package rs.ac.uns.ftn.informatics.semantic_web.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
public @Data class RoomsInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Integer beds;
	private Integer noRooms;
	
	@ManyToOne
	private Hotel hotel;
	
}
