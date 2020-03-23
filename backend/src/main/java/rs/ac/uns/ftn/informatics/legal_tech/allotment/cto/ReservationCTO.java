package rs.ac.uns.ftn.informatics.legal_tech.allotment.cto;

import java.sql.Timestamp;

import lombok.Data;

public @Data class ReservationCTO {

	private Timestamp from;
	private Timestamp to;
	private Integer beds;
	private Long noRooms;
	private Integer priceType; 
	private Integer kids;
	
}
