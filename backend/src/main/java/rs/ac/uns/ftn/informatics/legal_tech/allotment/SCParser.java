package rs.ac.uns.ftn.informatics.legal_tech.allotment;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.util.Arrays;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.cto.ReservationCTO;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.dto.ReservationDTO;

public class SCParser {

	private SCParser() {}
	
	// *****************************
	// RoomsInfo
	// *****************************
	
	
	
	// *****************************
	// Reservations
	// *****************************
	
	private final static int RESERVATION_PARSE_STEP = 32 * 8;
	
	public static List<ReservationDTO> parseReservations(byte[] bytes) {		
		List<ReservationDTO> reservations = new ArrayList<ReservationDTO>();
		
		int size = bytes.length / RESERVATION_PARSE_STEP;
		
		for (int i=0; i<size; ++i) {
			reservations.add(SCParser.parseReservation(Arrays.copyOfRange(bytes, i*RESERVATION_PARSE_STEP, (i+1)*RESERVATION_PARSE_STEP)));
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
		Timestamp fromDate = new Timestamp(bi.longValue()*1000);
		
		// Get to date
		bi = new BigInteger(Arrays.copyOfRange(bytes, 32*2, 32*3));
		Timestamp toDate = new Timestamp(bi.longValue()*1000);
		
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
		
		c.setId(id);
		c.setFrom(fromDate);
		c.setTo(toDate);
		c.setRooms(rooms);
		c.setPrice(price);
		c.setPriceType(type);
		c.setProvision(provision);
		c.setBeds(beds);
		
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
	
		return data;
		
	}
	
	
}
