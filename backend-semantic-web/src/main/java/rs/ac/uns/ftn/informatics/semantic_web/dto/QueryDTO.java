package rs.ac.uns.ftn.informatics.semantic_web.dto;

import java.util.Date;

import lombok.Data;

public @Data class QueryDTO {

	private Long startDate;
	private Long endDate;
	private String location;
	
}
