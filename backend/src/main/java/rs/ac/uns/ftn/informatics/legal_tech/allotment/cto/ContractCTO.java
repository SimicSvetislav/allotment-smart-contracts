package rs.ac.uns.ftn.informatics.legal_tech.allotment.cto;

import java.math.BigInteger;
import java.util.List;

import lombok.Data;

public @Data class ContractCTO {

	private Long id;
	
	private String agencyRepr;
	private String accomodationRepr;
	private BigInteger startDate;
	private BigInteger endDate;
	private List<BigInteger> hotels;
	private List<BigInteger> prices;
	private List<BigInteger> someContrains;
	private List<BigInteger> roomsInfo;
	private List<BigInteger> periods;
	private Boolean clause;
	private BigInteger advancePayment;
	private BigInteger commision;
	private BigInteger finePerBed;
	private String courtName;
	private String courtLocation;
	
	private Long agId;
	private Long accId;
	private String agName;
	private String accName;
	
	private List<String> hotelNames;
	
	public BigInteger agreementDate;
	public BigInteger balance;
	public BigInteger availablebalance;
	
}
