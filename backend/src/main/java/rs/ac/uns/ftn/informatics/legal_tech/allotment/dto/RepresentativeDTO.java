package rs.ac.uns.ftn.informatics.legal_tech.allotment.dto;

import lombok.Data;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Agency;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Organization;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Representative;

public @Data class RepresentativeDTO {
	private Long id;
	
	private String displayName;
	private String email;
	private String fullName;
	private String phoneNumber;
	
	private String type;
	private String orgName;
	private String orgAccount;
	private String orgAddress;
	private String orgCity;
	private String orgCountry;
	
	public RepresentativeDTO() {
	}
	
	public RepresentativeDTO(Representative repr) {
		this.setId(repr.getId());
		
		this.setDisplayName(repr.getDisplayName());
		this.setEmail(repr.getEmail());
		this.setFullName(repr.getFullName());
		this.setPhoneNumber(repr.getPhoneNumber());
		
		Organization org = repr.getRepresenting();
		
		this.setOrgAccount(org.getAccount());
		this.setOrgAddress(org.getAddress());
		this.setOrgCity(org.getCity());
		this.setOrgCountry(org.getCountry());
		this.setOrgName(org.getName());
		
		if (org instanceof Agency) {
			this.setType("Agency");
		} else {
			this.setType("Accomodation organization");
		}
	}
	
}
