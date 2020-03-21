package rs.ac.uns.ftn.informatics.legal_tech.allotment.dto;

import java.util.Date;

import lombok.Data;

public @Data class ReservationDTO {

	private Long id;
	private Date from;
	private Date to;
	private Integer beds; 
	private Integer rooms;
	private Long price;
	private Integer priceType;
	private Boolean provision;
}
