package rs.ac.uns.ftn.informatics.legal_tech.allotment.cto;

import java.math.BigInteger;
import java.util.List;

import lombok.Data;

public @Data class ContractCTO {

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
}