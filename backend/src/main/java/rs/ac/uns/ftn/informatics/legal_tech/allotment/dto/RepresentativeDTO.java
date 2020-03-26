package rs.ac.uns.ftn.informatics.legal_tech.allotment.dto;

import lombok.Data;

public @Data class RepresentativeDTO {

	private Long id;
	
	private String displayName;
	private String email;
	private String fullName;
	private String phoneNumber;
	
	private Integer type;
	private String orgName;
	private String orgAccount;
	private String orgAddress;
	private String orgCity;
	private String orgCountry;
	
}
