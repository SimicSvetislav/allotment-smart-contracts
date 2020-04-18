package rs.ac.uns.ftn.informatics.legal_tech.allotment.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.el.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.exceptions.ContractCallException;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.SCParser;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.contracts.Allotment;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.cto.ContractCTO;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.cto.InfoEvent;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.cto.ReservationCTO;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.dto.DateRange;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.dto.ReservationDTO;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.dto.RoomsInfoDTO;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Accomodation;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Account;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Agency;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Contract;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.ContractRoomsInfo;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Hotel;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.AccomodationRepository;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.AccountsRepository;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.AgencyRepository;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.ContractRepository;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.ContractRoomsInfoRepository;

@Service
public class ContractService {

	private Long PLATFORM_ACCOUNT = 10L;
	private BigInteger GAS_PRICE = BigInteger.valueOf(32000000000L);
	private BigInteger GAS_LIMIT = BigInteger.valueOf(6300000L);
	
	// private String PRIVATE_KEY="0x29b269ae87db6e792a0aaff8be7429e634aadf273266a7fb6589a34e0191ab68";
	// private String ADDRESS="0x11eccd64a00f8c0a1f81180ced0d538072138dbb";
	// Credentials credentials = Credentials.create(PRIVATE_KEY);
	
	@Autowired
	private Web3j web3j;
	
	@Autowired
	private ContractRepository repository;
	
	@Autowired
	private ContractRoomsInfoRepository contractRoomsInfoRepository;
	
	@Autowired
	private RepresentativeService reprService;
	
	@Autowired
	private HotelService hotelService;
	
	@Autowired
	private AgencyRepository agRepo;
	
	@Autowired
	private AccomodationRepository accRepo;
	
	@Autowired
	private AccountsRepository accountRepo;
	
	@SuppressWarnings("deprecation")
	@Transactional
	public String deployAllotment(ContractCTO contract) {
		
		Allotment deployedContract = null;
		
		try {
			deployedContract = Allotment.deploy(web3j, reprService.getCredentials(PLATFORM_ACCOUNT), 
					GAS_PRICE, GAS_LIMIT,
					contract.getAgencyRepr(), 
					contract.getAccomodationRepr(),
					contract.getStartDate(), contract.getEndDate(), 
					contract.getHotels(), contract.getPrices(), 
					contract.getSomeContrains(), contract.getRoomsInfo(), 
					contract.getPeriods(), contract.getClause(), 
					contract.getAdvancePayment(), contract.getCommision(), 
					contract.getFinePerBed(), contract.getCourtName(),
					contract.getCourtLocation() 
					).send();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deploy failed");
			// return "Deploy failed";
		}
		
		if (contract.getId() != null && contract.getId() > 0) {
			contractRoomsInfoRepository.deleteByContract_id(contract.getId());
		}
		
		Contract dbContract = new Contract();
		dbContract.setId(contract.getId());
		dbContract.setAddress(deployedContract.getContractAddress());
		dbContract.setStatus("NEG");
		// dbContract.setSeen(false);
		
		dbContract.setStartDate(new Date(contract.getStartDate().longValue() * 1000));
		dbContract.setEndDate(new Date(contract.getEndDate().longValue() * 1000));
		
		Agency ag = agRepo.findByAddress(contract.getAgencyRepr());
		dbContract.setAgency(ag);
		
		Accomodation acc = accRepo.findByAccount(contract.getAccomodationRepr());
		dbContract.setAccomodation(acc);
		
		dbContract.setAgReprId(contract.getSomeContrains().get(0).longValue());
		dbContract.setAccReprId(contract.getSomeContrains().get(1).longValue());
		
		dbContract = repository.save(dbContract);
		System.out.println("Saved contract with ID = " + dbContract.getId());
		
		// Dodaj podatke o sobama u bazu
		List<BigInteger> biRoomInfo = contract.getRoomsInfo();
		for (int i=1; i<biRoomInfo.size(); ++i) {
			int rooms = biRoomInfo.get(i).intValue();
			if (rooms < 1) {
				continue;
			}
			ContractRoomsInfo cri = new ContractRoomsInfo();
			cri.setContract(dbContract);
			cri.setBeds(i);
			cri.setNoRooms(rooms);
			cri = contractRoomsInfoRepository.save(cri);
			System.out.println("Saved cri with ID = " +cri.getId());
		}
		
		
		EthFilter filter = new EthFilter( DefaultBlockParameterName.LATEST,
			    DefaultBlockParameterName.LATEST, deployedContract.getContractAddress());
		
		web3j.ethLogFlowable(filter).subscribe(event -> {
		    
			InfoEvent ie = SCParser.parseInfoEvent(event.getData());
			
			System.out.println(ie);
		});
		
		return deployedContract.getContractAddress();
	}
	
	@SuppressWarnings("deprecation")
	public String deployAllotment() {
		
		Allotment contract = null;
		
		List<BigInteger> hotels = new ArrayList<BigInteger>();
		hotels.add(bi(1));
		hotels.add(bi(2));
		
		List<BigInteger> prices = new ArrayList<BigInteger>();
		prices.add(bi(15));
		prices.add(bi(25));
		prices.add(bi(12));
		prices.add(bi(10));
		
		List<BigInteger> someConstrains = new ArrayList<BigInteger>();
		someConstrains.add(bi(8));
		someConstrains.add(bi(20));
		someConstrains.add(bi(30));
		someConstrains.add(bi(50));
		
		List<BigInteger> roomsInfo = new ArrayList<BigInteger>();
		roomsInfo.add(bi(20));
		roomsInfo.add(bi(3));
		roomsInfo.add(bi(4));
		roomsInfo.add(bi(3));
		
		List<BigInteger> periods = new ArrayList<BigInteger>();
		periods.add(bi(10));
		periods.add(bi(7));
		
		String courtName = "Osnovni sud";
		String courtLocation = "Novi Sad";
		
		try {
			contract = Allotment.deploy(web3j, reprService.getCredentials(PLATFORM_ACCOUNT), 
					DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT,
					reprService.getAccountAddress(1L), 
					reprService.getAccountAddress(2L),
					bi(1589981726), bi(1595252126), 
					hotels, prices, someConstrains, roomsInfo, periods,
					false, bi(30), bi(5), bi(10),
					courtName, courtLocation
					).send();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			contract._badOffSeasonMaxPenalty().send().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return contract.getContractAddress();
	}
	
	public String getAgencyRepr(String contractAddress) {
		String repr = null;
		
		@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				contractAddress, web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);

		try {
			repr = contract._agencyAddress().send().toString();
		} catch (ContractCallException e) {
			repr = "Contract does not exist!";
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return repr;
	}
	
	public String getAccRepr(String contractAddress) {
		String repr = null;
		
		@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				contractAddress, web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);

		try {
			repr = contract._accomodationAddress().send().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return repr;
	}
	
	/*
	public String delegateAgency(String contractAddress, String newRepr, Long id) {
		
		Credentials credentials = reprService.getCredentials(id);
		
		@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				contractAddress, web3j, credentials,
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);

		TransactionReceipt tr = null;
		System.out.println("Good credentials: " + credentials.getAddress().equals(getAgencyRepr(contractAddress)));
		try {
			tr = contract.delegateAgency(newRepr).send();
		} catch (Exception e) {
			// e.printStackTrace();
			return "";
		}
		System.out.println("Status OK: " + tr.isStatusOK());
		
		return getAgencyRepr(contractAddress);
		
	}
	
	public String delegateAccomodation(String contractAddress, String newRepr, Long id) {
		
		Credentials credentials = reprService.getCredentials(id);
		
		@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				contractAddress, web3j, credentials,
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);

		TransactionReceipt tr = null;
		System.out.println("Good credentials: " + credentials.getAddress().equals(getAccRepr(contractAddress)));
		try {
			tr = contract.delegateAccomodation(newRepr).send();
		} catch (Exception e) {
			// e.printStackTrace();
			return "";
		}
		System.out.println("Status OK: " + tr.isStatusOK());
		
		return getAccRepr(contractAddress);
	}*/
	
	public String agencyAgreed(String contractAddress, Long id) {
		String msg = null;
		
		@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				contractAddress, web3j, reprService.getCredentialsFromOrg(id),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);

		try {
			msg = contract.agencyAgreed(BigInteger.valueOf(id)).send().toString();
		} catch (Exception e) {
			System.out.println("Failed to confirm by agency!");
			//e.printStackTrace();
		}
		System.out.println("Returned: " + msg);
		
		try {
			msg = contract._agencyAgreed().send().toString();
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		
		return msg;
	}
	
	public String accAgreed(String contractAddress, Long userId) {
		String msg = null;
		
		@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				contractAddress, web3j, reprService.getCredentialsFromOrg(userId),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);

		try {
			msg = contract.accomodationAgreed(BigInteger.valueOf(userId)).send().toString();
		} catch (Exception e) {
			System.out.println("Failed to confirm by accomodation!");
			// e.printStackTrace();
		}
		System.out.println("Returned: " + msg);
		
		try {
			msg = contract._accomodationAgreed().send().toString();
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		
		try {
			System.out.println(contract._advancePayment().send().toString());
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		
		return msg;
	}

	
	private BigInteger bi(Integer i) {
		return BigInteger.valueOf(i);
	}

	public BigInteger getBalance(String address) {
		EthGetBalance ethGetBalance = null;
		try {
			ethGetBalance = web3j
					  .ethGetBalance(address, DefaultBlockParameterName.LATEST)
					  .sendAsync()
					  .get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		BigInteger wei = ethGetBalance.getBalance();
		
		return wei;
	}
	
	public Double getEtherBalance(String address) {
		
		double ether = -1;
		
		BigInteger wei = getBalance(address);
		
		BigInteger factor = new BigInteger("1000000000000000000");
		ether = wei.doubleValue() / factor.doubleValue();
		
		return ether;
		
	}
	
	// Salje jedan ether na zeljeni nalog
	public String transfer(String from, String to, Long userId) {
		TransactionManager transactionManager = new RawTransactionManager(
                web3j,
                reprService.getCredentials(userId)
        );

        Transfer transfer = new Transfer(web3j, transactionManager);

        TransactionReceipt transactionReceipt = null;
		try {
			transactionReceipt = transfer.sendFunds(
			        to,
			        BigDecimal.ONE,
			        Convert.Unit.ETHER,
			        DefaultGasProvider.GAS_PRICE,
			        DefaultGasProvider.GAS_LIMIT
			).send();
		} catch (Exception e) {
			e.printStackTrace();
		}

        System.out.print("Transaction = " + transactionReceipt.getTransactionHash());
        
        return getEtherBalance(to).toString();
	}
	
	public String transferWeis(String from, String to, BigDecimal amount) {
		TransactionManager transactionManager = new RawTransactionManager(
                web3j,
                reprService.getCredentials(PLATFORM_ACCOUNT)
        );

        Transfer transfer = new Transfer(web3j, transactionManager);
        
        TransactionReceipt transactionReceipt = null;
		try {
			transactionReceipt = transfer.sendFunds(
			        to,
			        amount,
			        Convert.Unit.WEI,
			        DefaultGasProvider.GAS_PRICE,
			        DefaultGasProvider.GAS_LIMIT
			).send();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't transfer funds");
		}

        System.out.print("Transaction = " + transactionReceipt.getTransactionHash());
        
        return getBalance(to).toString();
	}

	public Double getEtherBalanceAccount(Long id) {
		
		String address = reprService.getAccountAddress(id);
		
		Double balance =  getEtherBalance(address);
		
		return balance;
	}
	
	public BigInteger getWeiBalanceAccount(Long id) {
		
		String address = reprService.getAccountAddress(id);
		
		BigInteger balance =  getBalance(address);
		
		return balance;
	}
	
	
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public String reserve(String contractAddress, Long repr, ReservationCTO res) {
		
		String msg = null;
		
		@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				contractAddress, web3j, reprService.getCredentials(repr),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);

		TransactionReceipt tr = null;
		
		try {
			List<BigInteger> serr = SCParser.serializeReservation(res);
			System.out.println(serr);
			tr = contract.reserve(serr).send();
			msg = "RESERVED";
		} catch (Exception e) {
			System.out.println("Failed to reserve!");
			//e.printStackTrace();
		}
		System.out.println("Returned: " + tr);
		
		return msg;
		
	}

	/*public String getRess(Integer beds, String contractAddress) {
	
		String  ret = "";
		
		@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				contractAddress, web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
		
		BigInteger bi = null;
		try {
			bi = contract.getAA(bi(beds)).send();
		} catch (Exception e) {
			System.out.println("Failed to get reservations as bytes!");
			e.printStackTrace();
		}
		System.out.println("Ret: " + bi);
		
		byte[] retVal = null;
		try {
			retVal = contract.getResAsBytes(bi(beds)).send();
		} catch (Exception e) {
			System.out.println("Failed to get reservations as bytes!");
			e.printStackTrace();
		}
		
		System.out.println("Size: " + retVal.length);
		
 		List<ReservationDTO> reservations = SCParser.parseReservations(retVal);
		for (ReservationDTO r: reservations) {
			System.out.println(r);
		}
		
		
		ret = retVal.toString();
		System.out.println("Returned: " + retVal.toString());
		
		return ret;
	}*/
	
	public String getRessAll(String contractAddress) {
		String  ret = "";
		
		@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				contractAddress, web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
		
		byte[] retVal = null;
		try {
			retVal = contract.getAllResAsBytes().send();
		} catch (Exception e) {
			System.out.println("Failed to get reservations as bytes!");
			e.printStackTrace();
		}
		
		System.out.println("Size: " + retVal.length);

 		List<ReservationDTO> reservations = SCParser.parseReservations(retVal);
		for (ReservationDTO r: reservations) {
			ret += r + "\n";
			System.out.println(r);
		}
		
		
		System.out.println("Returned: " + retVal.toString());
	
		return ret;
	}
	
	public List<ReservationDTO> getAllReservations(Long contractId) {
		
		Contract c = findById(contractId);
		String contractAddress = c.getAddress();
		
		@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				contractAddress, web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
		
		byte[] retVal = null;
		try {
			retVal = contract.getAllResAsBytes().send();
		} catch (Exception e) {
			System.out.println("Failed to get reservations as bytes!");
			e.printStackTrace();
		}
		
		System.out.println("Size: " + retVal.length);

 		List<ReservationDTO> reservations = SCParser.parseReservations(retVal);
		for (ReservationDTO r: reservations) {
			System.out.println(r);
		}
		
		
		System.out.println("Returned: " + retVal.toString());
	
		return reservations;
	}

	
	public String transferOne(String address) {
		
		String msg = null;
		
		/*@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				address, web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
		*/
		
		TransactionManager transactionManager = new RawTransactionManager(
                web3j,
                reprService.getCredentials(PLATFORM_ACCOUNT)
        );
		
		Transfer transfer = new Transfer(web3j, transactionManager);

        TransactionReceipt transactionReceipt = null;
		try {
			transactionReceipt = transfer.sendFunds(
			        address,
			        BigDecimal.ONE,
			        Convert.Unit.ETHER,
			        DefaultGasProvider.GAS_PRICE,
			        DefaultGasProvider.GAS_LIMIT
			).send();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*try {
			tr = contract.transferOne().send();
			msg = "Success";
		} catch (Exception e) {
			msg = "Failed";
			System.out.println("Failed to transfer!");
			//e.printStackTrace();
		}*/
		
		System.out.println("Returned: " + transactionReceipt);
		
		return msg;
	}

	// **************************
	// Breaking agreement
	// **************************
	public String breakAgency(String contractAddress, Long userId) {
		
		String msg = "";
		
		@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				contractAddress, web3j, reprService.getCredentials(userId),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);

		TransactionReceipt tr = null;
		
		try {
			tr = contract.breakContract(BigInteger.valueOf(userId)).send();
			msg = "Success";
		} catch (Exception e) {
			msg = "Failed to break agreement";
			System.out.println("Failed to break agreement!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
			//e.printStackTrace();
		}
		System.out.println("Returned: " + tr);
		
		return msg;
	}
	
	public String breakAccomodation(String address, Long userId) {
		
		String msg = "";
		
		@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				address, web3j, reprService.getCredentials(userId),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);

		TransactionReceipt tr = null;
		
		try {
			tr = contract.breakContract(BigInteger.valueOf(userId)).send();
			msg = "Success";
		} catch (Exception e) {
			// msg = "Failed";
			// System.out.println("Failed to transfer!");
			//e.printStackTrace();
			
			msg = "Failed to withdraw";
			System.out.println("Failed to break agreement!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
		}
		System.out.println("Returned: " + tr);
		
		return msg;
	}

	// **************************
	// Getting info from contract
	// **************************
	
	@SuppressWarnings("unchecked")
	public String getHotelsInContract(String address) {
		String msg = "";
		
		@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				address, web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);

		List<BigInteger> hotels = new ArrayList<BigInteger>();
		
		try {
			hotels = contract.getHotels().send();
			msg = "Success";
		} catch (Exception e) {
			msg = "Failed";
			System.out.println("Failed to transfer!");
			return msg;
			//e.printStackTrace();
		}
		
		List<String> hotelNames = new ArrayList<String>();
		for (BigInteger id : hotels) {
			Hotel hotel = hotelService.findById(id.longValue());
			hotelNames.add(hotel.getName());
		}
		
		System.out.println("Returned: " + hotels);
		
		return hotelNames.toString();
	}

	public String getCourtInfo(String address) {
		
		String msg = "";
		String courtInfo = "";
		
		@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				address, web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);

		try {
			courtInfo = contract.getCourtInfo().send();
			msg = "Success";
		} catch (Exception e) {
			msg = "Failed";
			System.out.println("Failed to transfer!");
			return msg;
			//e.printStackTrace();
		}
		
		System.out.println("Returned: " + courtInfo);
		
		String[] parts = courtInfo.split("@");
		String courtName = parts[0];
		String courtLocation = parts[1];
		
		return courtName + ", " + courtLocation;
	}

	public String getRoomsInfo(String address) {
		String msg = "";
		
		@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				address, web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);

		byte[] bytes = null;
		
		try {
			bytes = contract.getRoomsInfo().send();
			msg = "Success";
		} catch (Exception e) {
			msg = "Failed";
			System.out.println("Failed to transfer!");
			return msg;
			//e.printStackTrace();
		}
		
		List<RoomsInfoDTO> ri = SCParser.parseAllRoomsInfo(bytes);
	
		String ret = "";
		for (RoomsInfoDTO r: ri) {
			ret += r + "\n";
			System.out.println(r);
		}
		
		System.out.println("Returned:\n" + ret);
		
		return ret.toString();
	}

	@SuppressWarnings("unchecked")
	public ContractCTO getContractInfo(Contract dbContract) {
		
		String msg = "";
		
		Allotment contract;
		try {
			@SuppressWarnings("deprecation")
			Allotment contract2 = Allotment.load(
					dbContract.getAddress(), web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
			        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
			contract = contract2;
		} catch (Exception e) {
			System.err.println("Can't find contract with address " + dbContract.getAddress());
			return null;
		}
	
		byte[] bytes = null;
		
		try {
			bytes = contract.getContractInfoAsBytes().send();
			msg = "Success";
		} catch (Exception e) {
			msg = "Failed to get info!";
			System.out.println("Failed to get info!");
			return null;
			//throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
			//e.printStackTrace();
		}
		
		ContractCTO c = new ContractCTO();
		
		try {
			c = SCParser.parseContractInfoCTO(bytes);
		} catch (ParseException pe) {
			System.out.println(pe.getMessage());
			msg = "Parse failed";
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
		}
		
		// Get court info
		String courtInfo = "";
		try {
			courtInfo = contract.getCourtInfo().send();
			msg = "Success";
		} catch (Exception e) {
			msg = "Failed to get info!";
			System.out.println("Failed to get court info!");
			return null;
			// throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
			//e.printStackTrace();
		}
		
		String parts[] = courtInfo.split("@");
		c.setCourtName(parts[0]);
		c.setCourtLocation(parts[1]);
		
		
		// Get rooms info
		try {
			bytes = contract.getRoomsInfo().send();
			msg = "Success";
		} catch (Exception e) {
			msg = "Failed to get court info!";
			System.out.println("Failed to get court info!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
			//e.printStackTrace();
		}
		
		List<RoomsInfoDTO> riList = SCParser.parseAllRoomsInfo(bytes);
		
		Short maxBeds =  Collections.max(riList, Comparator.comparing(ri -> ri.getBeds())).getBeds();
		
		List<BigInteger> newList = new ArrayList<BigInteger>(Collections.nCopies(maxBeds+1, BigInteger.ZERO));
		int totalBeds = 0;
		
		for (int i=1; i<=maxBeds; ++i) {
			for (RoomsInfoDTO ri: riList) {
				if (ri.getBeds() == i) {
					newList.set(i, BigInteger.valueOf(ri.getRooms()));
					totalBeds += ri.getBeds() * ri.getRooms();
					break;
				}
			}
		}
		
		newList.set(0, BigInteger.valueOf(totalBeds));
		
		c.setRoomsInfo(newList);
		
		// Agency and accomodation ID
		// Contract dbContract = contractRepo.findByAddress(address);
		
		c.setAgId(dbContract.getAgency().getId());
		c.setAccId(dbContract.getAccomodation().getId());
		
		// Get hotels
		List<BigInteger> hotelIds = new ArrayList<BigInteger>();
		try {
			hotelIds = contract.getHotels().send();
			msg = "Success";
		} catch (Exception e) {
			msg = "Failed to get hotels info!";
			System.out.println("Failed to get hotels info!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
			//e.printStackTrace();
		}
		
		List<String> hotelNames = new ArrayList<String>();
		
		for (int i = 0; i < hotelIds.size(); ++i) {
			Hotel hotel = hotelService.findById(hotelIds.get(i).longValue());
			hotelNames.add(hotel.getName());
		}
		
		c.setHotels(hotelIds);
		c.setHotelNames(hotelNames);
		c.setId(dbContract.getId());
		c.setAccName(dbContract.getAccomodation().getName());
		c.setAgName(dbContract.getAgency().getName());
		
		c.setAgencyRepr(dbContract.getAgReprId().toString());
		c.setAccomodationRepr(dbContract.getAccReprId().toString());
		
		System.out.println(c);
		
		return c;
	}

	public String withdraw(DateRange range, String address, Long userId) {
		
		String msg = "";
		
		@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				address, web3j, reprService.getCredentialsFromOrg(userId),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
		
		
		TransactionReceipt tr = null;
		
		Long startTimestamp = range.getStartDate().getTime() / 1000;
		BigInteger startDate = BigInteger.valueOf(startTimestamp);
		
		Long endTimestamp = range.getEndDate().getTime() / 1000;
		BigInteger endDate = BigInteger.valueOf(endTimestamp);
				
		try {
			tr = contract.withdrawRooms(startDate, endDate).send();
			msg = "Success";
		} catch (Exception e) { 
			msg = "Failed to withdraw";
			System.out.println("Failed to withdraw!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
		}
		
		System.out.println(tr);
		
		return msg;
	}
	
	public String getWithdrawals(String contractAddress) {
		
		String msg = "";
		
		@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				contractAddress, web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);

		byte[] bytes = null;
		
		try {
			bytes = contract.getWithdrawalsAsBytes().send();
			msg = "Success getting withdrawals";
		} catch (Exception e) {
			msg = "Failed getting withdrawals";
			e.printStackTrace();
			System.out.println("Failed to get withdrawals!");
			return msg;
		}
		
		List<DateRange> withdrawals = SCParser.parseWithdrawals(bytes);

		for (DateRange dateRange : withdrawals) {
			System.out.println(dateRange);
		}
		
		return withdrawals.toString();	
	}


	public List<Contract> findByAcc(Long acc_id) {
		return repository.findByAccomodation_id(acc_id);
	}
	
	public List<Contract> findByAccActive(Long acc_id) {
		
		List<Contract> contracts = repository.findByAccomodation_id(acc_id);
				
		List<Contract> activeContracts = contracts.stream().filter(c -> c.getStatus().equals("NEG") || c.getStatus().equals("COW")).collect(Collectors.toList());
				
		return  activeContracts;
	}
	
	public List<ContractRoomsInfo> findRIByContract_id(Long contract_id) {
		return contractRoomsInfoRepository.findByContract_id(contract_id);
	}
	
	/*private String returnErrorMessage(int code) {
		
		String msg = "Error occured";
		
		switch (code) {
		case 1:
			msg = "Not enough ether, please send more!";
			break;

		case 2:
			msg = "Error code 2";
			break;
		case 3:
			msg = "Error code 3";
			break;
		case 4:
			msg = "Error code 4";
			break;
		default:
			msg += " (code unknown)";
			break;
		}
		
		return msg;
		
	}*/

	public String getinitialTransferValue(String address) {
		String msg = "";
		
		@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				address, web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
		
		
		BigInteger bi = null;
		try {
			bi = contract.getInitialTransferValue().send();
			msg = "Success";
		} catch (Exception e) {
			msg = "Failed";
			System.out.println("Failed to withdraw!");
			return msg;
		}
		
		System.out.println("Should be transfered -> " + bi);
		
		return bi.toString();
	}

	public Contract findById(Long id) {
		return repository.findById(id).get();
	}

	public List<Contract> getContractsByOrg(Long org_id) {
		List<Contract> contracts = repository.findContractsByOrg(org_id);
		
		return contracts;
	}
	
	public List<ContractCTO> getContractsByOrgAndStatus(Long org_id, String status) {
		List<Contract> contracts = repository.findContractsByOrgAndStatus(org_id, status);
		
		List<ContractCTO> ctos = contracts.parallelStream().map(c -> getContractInfo(c)).
				filter(c -> c!=null).
				collect(Collectors.toList());
		
		return ctos;
	}
	
	public Contract findContractByAddress(String address) {
		
		return repository.findByAddress(address);
		
	}

	public String rejectProposal(Long id) {
		
		Contract c = findById(id);
		
		c.setStatus("REJ");
		c.setAddress("0x0");
		
		repository.save(c);
		
		return "Proposal rejected";
	}

	public Contract acceptProposal(Long id, Long repr) {
		Contract c = findById(id);
		
		c.setStatus("COW");
		
		ContractCTO cto = getContractInfo(c);
		
		// TODO Promenjeno
		Accomodation accomod = accRepo.findById(cto.getAccId()).get();
		Account accAccount = accountRepo.findByAccount(accomod.getAccount()).get(0);
		
		Agency agen = agRepo.findById(cto.getAgId()).get();
		Account agAccount = accountRepo.findByAccount(agen.getAccount()).get(0);
		
		String accAddress = accAccount.getAccount();
		String agAddress = agAccount.getAccount();
		String contractAddress = c.getAddress();
		
		BigDecimal guarantee = BigDecimal.valueOf(cto.getFinePerBed().longValueExact() * cto.getRoomsInfo().get(0).longValueExact());
		BigDecimal guaranteeAgency = BigDecimal.valueOf(guarantee.longValue() + cto.getAdvancePayment().longValue());
		
		String balance = transferWeis(accAddress, contractAddress, guarantee);
		System.out.println(balance);
		balance = transferWeis(agAddress, contractAddress, guaranteeAgency);
		System.out.println(balance);
		
		if (c.getAccReprId()==0) {
			c.setAccReprId(repr);
			
			accAgreed(c.getAddress(), repr);
			
		} else {
			c.setAgReprId(repr);
			
			agencyAgreed(c.getAddress(), repr);
		}
		
		c = repository.save(c); 
		
		return c;
	}
	
	public String verify(Long reservationId, Long contractId, Long beds, Long agencyRepresentativeId) {
		String msg = "";
		
		Contract dbContract = findById(contractId);
		
		@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				dbContract.getAddress(), web3j, reprService.getCredentials(agencyRepresentativeId),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
		
		
		TransactionReceipt tr = null;
		try {
			tr = contract.verifyRoomingList(BigInteger.valueOf(beds), BigInteger.valueOf(reservationId)).send();
			msg = "Success";
		} catch (Exception e) {
			msg = "Failed to verify!";
			System.out.println("Failed to verify!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
		}
		
		System.out.println(tr);
		
		return msg;
	}
	
}
