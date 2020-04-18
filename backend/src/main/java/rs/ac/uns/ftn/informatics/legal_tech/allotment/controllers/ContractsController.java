package rs.ac.uns.ftn.informatics.legal_tech.allotment.controllers;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.cto.ContractCTO;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.cto.ReservationCTO;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.dto.ContractAddressPair;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.dto.DateRange;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.dto.ReservationDTO;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.dto.TransferPair;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Accomodation;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Agency;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Contract;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.ContractRoomsInfo;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Hotel;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Organization;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Representative;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.RoomsInfo;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.AccomodationRepository;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.AgencyRepository;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.services.ContractService;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.services.HotelService;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.services.RepresentativeService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/web3j")
public class ContractsController {
	
	@Autowired
	private ContractService service;
	
	@Autowired
	private HotelService hotelService;
	
	@Autowired
	private AccomodationRepository accRepo;
	
	@Autowired
	private AgencyRepository agRepo;
	
	@Autowired
	private RepresentativeService reprService;
	
	@GetMapping(path="/deployA")
	public ResponseEntity<String> deployA() {
		
		String msg = service.deployAllotment();
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/getAgr/{address}")
	public ResponseEntity<String> getAgr(@PathVariable("address") String address) {
		
		String msg = service.getAgencyRepr(address);
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/getAcr/{address}")
	public ResponseEntity<String> getAcr(@PathVariable("address") String address) {
		
		String msg = service.getAccRepr(address);
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
		
	}
	
	/*
	@PostMapping(path="/delegateAg/(userId)")
	public ResponseEntity<String> delegateAgency(
			@RequestBody ContractAddressPair pair,
			@PathVariable("userId") Long userId) {
		
		String msg = service.delegateAgency(pair.getContractAddress(), pair.getUserAddress(), userId);
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
		
	}
	
	@PostMapping(path="/delegateAcc/{userId}")
	public ResponseEntity<String> delegateAcc(
			@RequestBody ContractAddressPair pair,
			@PathVariable("userId") Long userId) {
		
		String msg = service.delegateAccomodation(pair.getContractAddress(), pair.getUserAddress(), userId);
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
		
	}*/
	
	@PostMapping(path="/agAgreed/{userId}")
	public ResponseEntity<String> agencyAgreed(
			@RequestBody ContractAddressPair pair,
			@PathVariable Long userId) {
		
		String msg = service.agencyAgreed(pair.getContractAddress(), userId);
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
		
	}
	
	@PostMapping(path="/accAgreed/{userId}")
	public ResponseEntity<String> accAgreed(
			@RequestBody ContractAddressPair pair,
			@PathVariable("userId") Long userId) {
		
		String msg = service.accAgreed(pair.getContractAddress(), userId);
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
		
	}
	
	@PostMapping(path="/balanceEther")
	public ResponseEntity<Double> balanceEther(@RequestBody ContractAddressPair pair) {
		
		Double balance = service.getEtherBalance(pair.getContractAddress());
		
		return new ResponseEntity<Double>(balance, HttpStatus.OK);
		
	}
	
	@PostMapping(path="/balanceWei")
	public ResponseEntity<String> balance(@RequestBody ContractAddressPair pair) {
		
		String balance = service.getBalance(pair.getContractAddress()).toString();
		
		return new ResponseEntity<String>(balance, HttpStatus.OK);
		
	}
	
	@PostMapping(path="/transfer/{user}")
	public ResponseEntity<String> transfer(@RequestBody TransferPair pair, @PathVariable("user") Long user) {
		
		String balance = service.transfer(pair.getFrom(), pair.getTo(), user);
		
		return new ResponseEntity<String>(balance, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/balanceUser/{id}")
	public ResponseEntity<Double> balanceUser(@PathVariable("id") Long id) {
		
		Double balance = service.getEtherBalanceAccount(id);
		
		return new ResponseEntity<Double>(balance, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/balances")
	public ResponseEntity<Double[]> balancesUser() {
		
		Double[] balances = new Double[10];
		
		for (int i=0; i<10; i++) {
			Double balance = service.getEtherBalanceAccount(new Long(i+1));
			balances[i] = balance;
		}
		
		return new ResponseEntity<Double[]>(balances, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/balancesWei")
	public ResponseEntity<BigInteger[]> balancesWei() {
		
		BigInteger[] balances = new BigInteger[10];
		
		for (int i=0; i<10; i++) {
			BigInteger balance = service.getWeiBalanceAccount(new Long(i+1));
			balances[i] = balance;
		}
		
		return new ResponseEntity<BigInteger[]>(balances, HttpStatus.OK);
		
	}
	
	/*
	@PostMapping(path="/ress/{beds}")
	public ResponseEntity<String> getRess(@RequestBody ContractAddressPair pair, @PathVariable("beds") Integer beds) {
		
		
		String retVal = service.getRess(beds, pair.getContractAddress());

		
		return new ResponseEntity<String>(retVal, HttpStatus.OK);
		
	}*/
	
	@PostMapping(path="/ressAll")
	public ResponseEntity<String> getRessAll(@RequestBody ContractAddressPair pair) {
		
		
		String retVal = service.getRessAll(pair.getContractAddress());

		
		return new ResponseEntity<String>(retVal, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/reservations/{contractId}")
	public ResponseEntity<List<ReservationDTO>> getAllReservations(@PathVariable("contractId") Long contractId) {
		
		List<ReservationDTO> reservationsList = service.getAllReservations(contractId);
		
		return new ResponseEntity<List<ReservationDTO>>(reservationsList, HttpStatus.OK);   	
	}
	
	@PostMapping(path="/reserve/{repr}/{address}")
	public ResponseEntity<String> reserve(
			@RequestBody ReservationCTO res, 
			@PathVariable("address") String address,
			@PathVariable("repr") Long repr) {
		
		
		String retVal = service.reserve(address, repr, res);

		
		return new ResponseEntity<String>(retVal, HttpStatus.OK);
		
	}
	
	@PostMapping(path="/reservation/{cid}/{repr}")
	public ResponseEntity<String> reserveFrontend(
			@RequestBody ReservationCTO res, 
			@PathVariable("cid") Long cid,
			@PathVariable("repr") Long repr) {
		
		Contract c = service.findById(cid);
		
		String retVal = service.reserve(c.getAddress(), repr, res);

		return new ResponseEntity<String>(retVal, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/t1/{contract}")
	public ResponseEntity<String> reserve(@PathVariable("contract") String address) {
		
		
		String retVal = service.transferOne(address);

		
		return new ResponseEntity<String>(retVal, HttpStatus.OK);
		
	}

	@PostMapping(path="/breakAg/{userId}")
	public ResponseEntity<String> breakAgency(
			@RequestBody ContractAddressPair pair,
			@PathVariable("userId") Long userId) {
		
		
		String retVal = service.breakAgency(pair.getContractAddress(), userId);

		
		return new ResponseEntity<String>(retVal, HttpStatus.OK);
		
	}
	
	@PostMapping(path="/breakAcc/{userId}")
	public ResponseEntity<String> breakAccomodation(
			@RequestBody ContractAddressPair pair,
			@PathVariable("userId") Long userId) {
		
		
		String retVal = service.breakAccomodation(pair.getContractAddress(), userId);

		
		return new ResponseEntity<String>(retVal, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/breakAg/{cid}/{repr}")
	public ResponseEntity<String> breakAgencyFrontend(
			@PathVariable("cid") Long cid,
			@PathVariable("repr") Long repr) {
		
		Contract c = service.findById(cid);
		
		String retVal = service.breakAgency(c.getAddress(), repr);

		return new ResponseEntity<String>(retVal, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/breakAcc/{cid}/{repr}")
	public ResponseEntity<String> breakAccomodationFrontend(
			@PathVariable("cid") Long cid,
			@PathVariable("repr") Long repr) {
		
		
		Contract c = service.findById(cid);
		
		String retVal = service.breakAccomodation(c.getAddress(), repr);

		
		return new ResponseEntity<String>(retVal, HttpStatus.OK);
		
	}
	
	@PostMapping(path="/deployA")
	public ResponseEntity<String> deployParameters(
			@RequestBody ContractCTO contract) {
		
		
		Long orgId = null; 
		
		// Proverava da li su navedeni predstavnici
		// Predstavnik agencije
		Long agrId = contract.getSomeContrains().get(0).longValue();
		if (agrId != 0L) {
			Representative repr = reprService.getById(agrId);
			Organization org = repr.getRepresenting();
			contract.setAgencyRepr(org.getAccount());
		}
		
		// Predstavnik ugostitelja
		Long accrId = contract.getSomeContrains().get(1).longValue();
		if (contract.getSomeContrains().get(1).longValue() != 0L) {
			Representative repr = reprService.getById(accrId);
			Organization org = repr.getRepresenting();
			orgId = org.getId();
			contract.setAccomodationRepr(org.getAccount());
		}
		
		// Proverava da li je naveden ID organizacije
		Long agId = contract.getAgId();
		if (agId != null && agId != 0) {
			Agency ag = agRepo.findById(agId).get();
			contract.setAgencyRepr(ag.getAccount());
		}
		
		Long accId = contract.getAccId();
		if (accId != null && accId != 0) {
			Accomodation acc = accRepo.findById(accId).get();
			orgId = acc.getId();
			contract.setAccomodationRepr(acc.getAccount());
		}
		
		// Proveriti da li hoteli pripadaju istoj organizaciji
		List<BigInteger> hotels = contract.getHotels();
		if (hotels.size() <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hotels not listed");
			// return new ResponseEntity<String>("Hotels not listed", HttpStatus.BAD_REQUEST);
		}
		
		// Raspolozivi kapacitet u hotelima
		// kljuc - broj kreveta
		// vredost - broj soba sa odredjenim brojem kreveta
		Map<Integer, Integer> capacity = new HashMap<Integer, Integer>();
		
		for (BigInteger id: hotels) {
			
			Hotel hotel = hotelService.findById(id.longValue());
			Accomodation org = hotel.getOrg();
			if (org.getId() != orgId) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hotels do not belong to organization");
				// return new ResponseEntity<String>("Hotels do not belong to organization", HttpStatus.BAD_REQUEST);
			}
			
			// Dodaj odgovarajuce kapacitete raspolozive u hotelu
			List<RoomsInfo> riList = hotelService.getRoomsInfo(hotel.getId());
			for (RoomsInfo ri : riList) {
				capacity.put(ri.getBeds(), capacity.getOrDefault(ri.getBeds(), 0) + ri.getNoRooms());
			}
			
		}
		
		// Belezenje smestaja koji je zauzet u zeljenom terminu
		Map<Integer, Integer> occupied = new HashMap<Integer, Integer>();
		List<Contract> deployedContracts = service.findByAccActive(orgId);
		
		for (Contract c : deployedContracts) {
			
			Date cStartDate = c.getStartDate();
			Date cEndDate = c.getEndDate();
			
			Date ctoStartDate = new Date(contract.getStartDate().longValue() * 1000);
			Date ctoEndDate = new Date(contract.getEndDate().longValue() * 1000);
			
			// Dodaj u zauzete samo ako se 
			if (!(cStartDate.after(ctoEndDate) ||
				  isSameDay(cStartDate, ctoEndDate) ||
				  ctoStartDate.after(cEndDate) ||
				  isSameDay(ctoStartDate, cEndDate)
				)) {
				List<ContractRoomsInfo> cris = service.findRIByContract_id(c.getId());
				for (ContractRoomsInfo cri : cris) {
					occupied.put(cri.getBeds(), occupied.getOrDefault(cri.getBeds(), 0) + cri.getNoRooms());
				}
			}
		}

		List<BigInteger> roomsInfo = contract.getRoomsInfo();
		Map<Integer, Integer> lookingFor = new HashMap<Integer, Integer>();
		for (int i=1; i<roomsInfo.size(); ++i) {
			int rooms = roomsInfo.get(i).intValue();
			if (rooms != 0) {
				lookingFor.put(i, rooms);
			}
		}
		
		System.out.println("Capacity: " + capacity);
		System.out.println("Occupied: " + occupied);
		System.out.println("Looking for: " + lookingFor);
		
		// Preskace se prvi broj jer je to ukupan broj kreveta
		for (Integer bedNum : lookingFor.keySet()) {
			int demand = lookingFor.get(bedNum);
			
			int totalCap = capacity.getOrDefault(bedNum, 0);
			int rented = occupied.getOrDefault(bedNum, 0);
			
			if (demand > totalCap - rented) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rooms not available for rent!");
				// return new ResponseEntity<String>("Rooms not available for rent!", HttpStatus.BAD_REQUEST);
			}	
		}
		
		String retVal = service.deployAllotment(contract);
		
		if (contract.getSomeContrains().get(0).longValue() != 0) {
			service.accAgreed(retVal, contract.getSomeContrains().get(0).longValue());
		} else if (contract.getSomeContrains().get(1).longValue() != 0) {
			service.agencyAgreed(retVal, contract.getSomeContrains().get(1).longValue());
		}
		
		return new ResponseEntity<String>(retVal, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/hotels/{contract}")
	public ResponseEntity<String> getHotelsInContract(@PathVariable("contract") String address) {
		
		
		String retVal = service.getHotelsInContract(address);

		
		return new ResponseEntity<String>(retVal, HttpStatus.OK);
		
	}

	@GetMapping(path="/courtInfo/{contract}")
	public ResponseEntity<String> getCourtInfo(@PathVariable("contract") String address) {
		
		
		String retVal = service.getCourtInfo(address);

		
		return new ResponseEntity<String>(retVal, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/roomsInfo/{contract}")
	public ResponseEntity<String> getRoomsInfo(@PathVariable("contract") String address) {
		
		
		String retVal = service.getRoomsInfo(address);

		
		return new ResponseEntity<String>(retVal, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/contractInfo/{contract}")
	public ResponseEntity<String> getContractInfo(@PathVariable("contract") String address) {
		
		Contract c = service.findContractByAddress(address);
		
		String retVal = service.getContractInfo(c).toString();
		
		return new ResponseEntity<String>(retVal, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/contract/{id}")	
	public ResponseEntity<ContractCTO> getFullContractInfoId(@PathVariable("id") Long id) {
		
		Contract c = service.findById(id);
		
		ContractCTO retVal = service.getContractInfo(c);;
		
		return new ResponseEntity<ContractCTO>(retVal, HttpStatus.OK);
		
	}
	
	@PostMapping(path="/withdraw/{id}/{address}")
	public ResponseEntity<String> withdraw(
			@RequestBody DateRange range,
			@PathVariable("id") Long userId,
			@PathVariable("address") String address) {
		
		
		String retVal = service.withdraw(range, address, userId);

		
		return new ResponseEntity<String>(retVal, HttpStatus.OK);
		
	}
	
	@PostMapping(path="/withdrawal/{cid}/{repr}")
	public ResponseEntity<String> withdrawFrontend (
			@RequestBody DateRange range,
			@PathVariable("cid") Long cid,
			@PathVariable("repr") Long repr) {
		
		
		Contract c = service.findById(cid);
		
		String retVal = service.withdraw(range, c.getAddress(), repr);

		return new ResponseEntity<String>(retVal, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/withdrawal/{contract}")
	public ResponseEntity<String> getWithdrawals(@PathVariable("contract") String contract) {
		
		
		String retVal = service.getWithdrawals(contract);

		
		return new ResponseEntity<String>(retVal, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/initialTransferValue/{contract}")
	public ResponseEntity<String> getinitialTransferValue(@PathVariable("contract") String contract) {
		
		
		String retVal = service.getinitialTransferValue(contract);

		
		return new ResponseEntity<String>(retVal, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/contracts/{id}/{status}")
	public ResponseEntity<List<ContractCTO>> getOrgContractsWithStatus(
			@PathVariable("id") Long id,
			@PathVariable("status") String status) {
		
		
		// List<Contract> list = service.getContractsByOrg(id);
		List<ContractCTO> list = service.getContractsByOrgAndStatus(id, status);
		
		return new ResponseEntity<List<ContractCTO>>(list, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/accept/{contract}/{repr}")
	public ResponseEntity<String> acceptProposal(
			@PathVariable("contract") Long contract,
			@PathVariable("repr") Long repr) {
		
		//Contract c = service.acceptProposal(contract, repr);
		service.acceptProposal(contract, repr);
		
		return new ResponseEntity<String>("Contract successfully concluded", HttpStatus.OK);
		
	}
	
	@GetMapping(path="/reject/{contract}")
	public ResponseEntity<String> rejectProposal(
			@PathVariable("contract") Long contract) {
		
		String msg = service.rejectProposal(contract);
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/verify/{contractId}/{reservationId}/{beds}/{agencyReprentativeId}")
	public ResponseEntity<String> verify(
			@PathVariable("contractId") Long contractId,
			@PathVariable("reservationId") Long reservationId,
			@PathVariable("beds") Long beds,
			@PathVariable("agencyReprentativeId") Long agencyReprentativeId) {
		
		String msg = service.verify(reservationId, contractId, beds, agencyReprentativeId);
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
	}
	
	private boolean isSameDay(Date date1, Date date2) {
	    SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
	    return fmt.format(date1).equals(fmt.format(date2));
	}
}
