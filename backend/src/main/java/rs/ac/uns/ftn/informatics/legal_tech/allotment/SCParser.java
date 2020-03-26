package rs.ac.uns.ftn.informatics.legal_tech.allotment;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.el.parser.ParseException;
import org.bouncycastle.util.Arrays;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.cto.InfoEvent;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.cto.ReservationCTO;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.dto.ContractDTO;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.dto.DateRange;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.dto.ReservationDTO;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.dto.RoomsInfoDTO;

public class SCParser {

	private SCParser() {}
	
	// *****************************
	// Contract info
	// *****************************
	
	public static InfoEvent parseInfoEvent(String data) throws Exception {
		InfoEvent ie = new InfoEvent();
		
		// Take code
		String codeStr = data.substring(0, 2+64);
		int code = Integer.decode(codeStr);
		
		String typeStr = "0x" + data.substring(2+64, 2+64*2);
		int type = Integer.decode(typeStr);
		
		if (type!=64) {
			throw new Exception("Event data not valid");
		}
		
		String lengthStr = "0x" + data.substring(2+64*2, 2+64*3);
		int length = Integer.decode(lengthStr);
		
		System.out.println(length);
		
		String ascii = "";
		for (int i=0; i<length; ++i) {
			String hexChar = "0x" + data.substring(2+64*3+i*2, 2+64*3+(i+1)*2);
			
			char c = (char)Integer.decode(hexChar).intValue();
			
			// System.out.println(c);
			
			ascii += c;
			
		}
		
		ie.setCode(code);
		ie.setMsg(ascii);
		
		return ie;
	}
	
	
	// *****************************
	// Contract info
	// *****************************
	
	private final static int CONTRACT_INFO_PARSE_STEP = 32 * 22;
	
	public static ContractDTO parseContractInfo(byte[] bytes) throws ParseException {
		
		ContractDTO cInfo = new ContractDTO();
		
		int size = bytes.length / CONTRACT_INFO_PARSE_STEP;
		
		if (size!=1) {
			throw new ParseException("Length of byte array not valid");
		}
				
		short i = 0;
		
		// Get start date
		BigInteger bi = getNextRange(bytes, i++);
		Date startDate = new Date(bi.longValue()*1000);
		
		// Get end date
		bi = getNextRange(bytes, i++);
		Date endDate = new Date(bi.longValue()*1000);
		
		// Get half-board price
		bi = getNextRange(bytes, i++);
		Long priceHB = bi.longValue();
		
		// Get full-board price
		bi = getNextRange(bytes, i++);
		Long priceFB = bi.longValue();
		
		
		
		// Get offseason price
		bi = getNextRange(bytes, i++);
		Long priceOS = bi.longValue();
		
		// Get kid price
		bi = getNextRange(bytes, i++);
		Long kidPrice = bi.longValue();
		
		// Get kid age limit
		bi = getNextRange(bytes, i++);
		Short kidAgeLimit = bi.shortValue();
		
		// Get discount gor small kids
		bi = getNextRange(bytes, i++);
		Short smallKidDiscount = bi.shortValue();

		
		
		// Get minimum offseason occupation needed
		bi = getNextRange(bytes, i++);
		Short offSeasonMinimum = bi.shortValue();
		
		// Get penalty to agency for bad offseason
		bi = getNextRange(bytes, i++);
		Short badOffseasonMaxPenalty = bi.shortValue();
		
		// Get withdrawal period
		bi = getNextRange(bytes, i++);
		Short withdrawalPeriod = bi.shortValue();
		
		// Get informin period
		bi = getNextRange(bytes, i++);
		Short informingPeriod = bi.shortValue();
		
		
		
		// Get if clause exists
		bi = getNextRange(bytes, i++);
		Boolean clause = bi.intValue() == 0 ? false : true;
		
		// Get advance payment
		bi = getNextRange(bytes, i++);
		Long advancePayment = bi.longValue();
		
		// Get commision
		bi = getNextRange(bytes, i++);
		Short commision = bi.shortValue();
		
		// Get fine per bed
		bi = getNextRange(bytes, i++);
		Long finePerBed = bi.longValue();
		
		
		
		// Get balance in wei
		bi = getNextRange(bytes, i++);
		Long balance = bi.longValue();
		
		// Get when consent of wills is acquired
		bi = getNextRange(bytes, i++);
		Date aDate = bi.longValue() != 0 ? new Date(bi.longValue()*1000) : null;
		
		bi = getNextRange(bytes, i++);
		Long identifiedAgRepr = bi.longValue() != 0 ? bi.longValue() : null;
		
		bi = getNextRange(bytes, i++);
		Long idetifiedAccRepr = bi.longValue() != 0 ? bi.longValue() : null;
		
		
		
		bi = getNextRange(bytes, i++);
		Date mainSeasonStart = new Date(bi.longValue()*1000);
		
		bi = getNextRange(bytes, i++);
		Date mainSeasonEnd = new Date(bi.longValue()*1000);
		
		
		
		cInfo.setStartDate(startDate);
		cInfo.setEndDate(endDate);
		cInfo.setPriceHB(priceHB);
		cInfo.setPriceFB(priceFB);
		
		cInfo.setPriceOS(priceOS);
		cInfo.setKidPrice(kidPrice);
		cInfo.setKidAgeLimit(kidAgeLimit);
		cInfo.setSmallKidDiscount(smallKidDiscount);
		
		cInfo.setOffSeasonMinimum(offSeasonMinimum);
		cInfo.setBadOffseasonMaxPenalty(badOffseasonMaxPenalty);
		cInfo.setWithdrawalPeriod(withdrawalPeriod);
		cInfo.setInformingPeriod(informingPeriod);
		
		cInfo.setClause(clause);;
		cInfo.setAdvancePayment(advancePayment);
		cInfo.setCommision(commision);
		cInfo.setFinePerBed(finePerBed);
		
		cInfo.setBalanceWei(balance);
		cInfo.setAgreementDate(aDate);
		cInfo.setIdentifiedAgRepr(identifiedAgRepr);
		cInfo.setIdetifiedAccRepr(idetifiedAccRepr);
		
		cInfo.setMainSeasonStart(mainSeasonStart);
		cInfo.setMainSeasonEnd(mainSeasonEnd);
		
		return cInfo;
		
	}
	
	private static BigInteger getNextRange(byte[] bytes, short i) {
		
		BigInteger bi = new BigInteger(Arrays.copyOfRange(bytes, 32*i, 32*(i+1)));
		
		return bi;
	}
	
	
	// *****************************
	// RoomsInfo
	// *****************************
	
	private final static int ROOMS_INFO_PARSE_STEP = 32 * 2;
	
	public static List<RoomsInfoDTO> parseAllRoomsInfo(byte[] bytes) {
		
		List<RoomsInfoDTO> info = new ArrayList<RoomsInfoDTO>();
		
		int size = bytes.length / ROOMS_INFO_PARSE_STEP;
		
		for (int i=0; i<size; ++i) {
			RoomsInfoDTO ri = parseRoomsInfo(Arrays.copyOfRange(bytes, i*ROOMS_INFO_PARSE_STEP, (i+1)*ROOMS_INFO_PARSE_STEP));
			info.add(ri);
		}
		
		return info;
		
	}
	
	private static RoomsInfoDTO parseRoomsInfo(byte[] riBytes) {
		RoomsInfoDTO ri = new RoomsInfoDTO();
		
		// Get beds
		BigInteger bi = new BigInteger(Arrays.copyOfRange(riBytes, 0, 32));
		Short beds = bi.shortValue();
		
		// Get rooms
		bi = new BigInteger(Arrays.copyOfRange(riBytes, 32, 32*2));
		Long rooms = bi.longValue();
		
		ri.setBeds(beds);
		ri.setRooms(rooms);
		
		return ri;
	}
	
	// *****************************
	// Reservations
	// *****************************

	private final static int RESERVATION_PARSE_STEP = 32 * 10;
	
	public static List<ReservationDTO> parseReservations(byte[] bytes) {		
		List<ReservationDTO> reservations = new ArrayList<ReservationDTO>();
		
		int size = bytes.length / RESERVATION_PARSE_STEP;
		
		for (int i=0; i<size; ++i) {
			reservations.add(parseReservation(Arrays.copyOfRange(bytes, i*RESERVATION_PARSE_STEP, (i+1)*RESERVATION_PARSE_STEP)));
		}
		
		return reservations;
	}
	
	public static ReservationDTO parseReservation(byte[] bytes) {
		
		ReservationDTO c = new ReservationDTO();
		
		// Get id
		BigInteger bi = new BigInteger(Arrays.copyOfRange(bytes, 0, 32));
		Long id = bi.longValue();
		
		// Get from date
		bi = new BigInteger(Arrays.copyOfRange(bytes, 32, 32*2));
		Date fromDate = new Date(bi.longValue()*1000);
		
		// Get to date
		bi = new BigInteger(Arrays.copyOfRange(bytes, 32*2, 32*3));
		Date toDate = new Date(bi.longValue()*1000);
		
		// Get number of rooms
		bi = new BigInteger(Arrays.copyOfRange(bytes, 32*3, 32*4));
		Integer rooms = bi.intValue();
		
		// Get price
		bi = new BigInteger(Arrays.copyOfRange(bytes, 32*4, 32*5));
		Long price = bi.longValue();
		
		// Get price type
		bi = new BigInteger(Arrays.copyOfRange(bytes, 32*5, 32*6));
		Integer type = bi.intValue();
		
		// Get provision
		bi = new BigInteger(Arrays.copyOfRange(bytes, 32*6, 32*7));
		Boolean provision = bi.intValue() == 0 ? false : true;
		
		// Get beds
		bi = new BigInteger(Arrays.copyOfRange(bytes, 32*7, 32*8));
		Integer beds = bi.intValue();
		
		// Is reservation in main season
		bi = new BigInteger(Arrays.copyOfRange(bytes, 32*8, 32*9));
		Boolean mainSeason = bi.intValue() == 0 ? false : true;
		
		// Get number of kids
		bi = new BigInteger(Arrays.copyOfRange(bytes, 32*9, 32*10));
		Integer kids = bi.intValue();
		
		c.setId(id);
		c.setFrom(fromDate);
		c.setTo(toDate);
		c.setRooms(rooms);
		c.setPrice(price);
		c.setPriceType(type);
		c.setProvision(provision);
		c.setBeds(beds);
		c.setMainSeason(mainSeason);
		c.setKids(kids);
		
		return c;
		
	}
	
	public static List<BigInteger> serializeReservation(ReservationCTO res) {
		
		List<BigInteger> data = new ArrayList<BigInteger>();
		 
		// From date
		BigInteger bi = BigInteger.valueOf((long)res.getFrom().getTime() / 1000);
		data.add(bi);
		
		// To date
		bi = BigInteger.valueOf((long)res.getTo().getTime() / 1000);
		data.add(bi);
		
		// Beds
		bi = BigInteger.valueOf(res.getBeds());
		data.add(bi);
		
		// Number Rooms
		bi = BigInteger.valueOf(res.getNoRooms());
		data.add(bi);
		
		// Price type
		bi = BigInteger.valueOf(res.getPriceType());
		data.add(bi);
	
		// Price type
		bi = BigInteger.valueOf(res.getKids());
		data.add(bi);
		
		return data;
		
	}
	
	// *****************************
	// Withdrawals
	// *****************************
	
	private static final Integer WITHDEAWALS_PARSE_STEP = 32 * 2;
	
	public static List<DateRange> parseWithdrawals(byte[] bytes) {
		
		List<DateRange> withdrawals = new ArrayList<DateRange>();
		
		int size = bytes.length / WITHDEAWALS_PARSE_STEP;
		
		for (int i=0; i<size; ++i) {
			DateRange dr = parseDateRange(Arrays.copyOfRange(bytes, i*WITHDEAWALS_PARSE_STEP, (i+1)*WITHDEAWALS_PARSE_STEP));
			withdrawals.add(dr);
		}
		
		return withdrawals;
		
	}

	public static DateRange parseDateRange(byte[] bytes) {
		
		DateRange dateRange = new DateRange();
		
		// Get start date
		BigInteger bi = new BigInteger(Arrays.copyOfRange(bytes, 0, 32));
		Date startDate = new Date(bi.longValue()*1000);
		
		// Get end date
		bi = new BigInteger(Arrays.copyOfRange(bytes, 32, 32*2));
		Date endDate = new Date(bi.longValue()*1000);
		
		dateRange.setStartDate(startDate);
		dateRange.setEndDate(endDate);
		
		return dateRange;
	}
	
	
}
