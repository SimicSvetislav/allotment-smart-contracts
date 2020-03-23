package rs.ac.uns.ftn.informatics.legal_tech.allotment.dto;

import java.util.Date;

import lombok.Data;

public @Data class ContractDTO {

	private Date startDate;
	private Date endDate;
	private Long priceHB;
	private Long priceFB;
	
	private Long priceOS;
	private Long kidPrice;
	private Short kidAgeLimit;
	private Short smallKidDiscount;
	
	private Short offSeasonMinimum;
	private Short badOffseasonMaxPenalty;
	private Short withdrawalPeriod;
	private Short informingPeriod;
	
	private Boolean clause;
	private Long advancePayment;
	private Short commision;
	private Long finePerBed;
	
	private Long balanceWei;	 
	private Date agreementDate;
	private Long identifiedAgRepr;
	private Long idetifiedAccRepr;
	
	private Date mainSeasonStart;
	private Date mainSeasonEnd;
	
}
