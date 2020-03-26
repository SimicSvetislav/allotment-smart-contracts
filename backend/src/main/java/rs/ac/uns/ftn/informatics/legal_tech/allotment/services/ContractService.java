package rs.ac.uns.ftn.informatics.legal_tech.allotment.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.el.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Credentials;
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
import rs.ac.uns.ftn.informatics.legal_tech.allotment.dto.ContractDTO;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.dto.DateRange;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.dto.ReservationDTO;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.dto.RoomsInfoDTO;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Accomodation;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Agency;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Contract;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.ContractRoomsInfo;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Hotel;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.AccomodationRepository;
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
	
	@SuppressWarnings("deprecation")
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
			return "Deploy failed";
		}
		
		Contract dbContract = new Contract();
		dbContract.setAddress(deployedContract.getContractAddress());
		dbContract.setStatus("NEG");
		
		dbContract.setStartDate(new Date(contract.getStartDate().longValue() * 1000));
		dbContract.setEndDate(new Date(contract.getEndDate().longValue() * 1000));
		
		Agency ag = agRepo.findByAddress(contract.getAgencyRepr());
		dbContract.setAgency(ag);
		
		Accomodation acc = accRepo.findByAccount(contract.getAccomodationRepr());
		dbContract.setAccomodation(acc);
		
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
			repr = contract._agencyRepr().send().toString();
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
			repr = contract._accomodationRepr().send().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return repr;
	}
	
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
	}
	
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
			List<BigInteger> serr =SCParser.serializeReservation(res);
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

	public String getRess(Integer beds, String contractAddress) {
	
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
	}
	
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

	public String transferOne(String address) {
		
		String msg = null;
		@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				address, web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);

		TransactionReceipt tr = null;
		
		try {
			tr = contract.transferOne().send();
			msg = "Success";
		} catch (Exception e) {
			msg = "Failed";
			System.out.println("Failed to transfer!");
			//e.printStackTrace();
		}
		System.out.println("Returned: " + tr);
		
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
			tr = contract.breakContractAgency().send();
			msg = "Success";
		} catch (Exception e) {
			msg = "Failed";
			System.out.println("Failed to transfer!");
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
			tr = contract.breakContractAccomodation().send();
			msg = "Success";
		} catch (Exception e) {
			msg = "Failed";
			System.out.println("Failed to transfer!");
			//e.printStackTrace();
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

	public String getContractInfo(String address) {
		
		String msg = "";
		
		@SuppressWarnings("deprecation")
		Allotment contract = Allotment.load(
				address, web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);

		byte[] bytes = null;
		
		try {
			bytes = contract.getContractInfoAsBytes().send();
			msg = "Success";
		} catch (Exception e) {
			msg = "Failed";
			System.out.println("Failed to get info!");
			return msg;
			//e.printStackTrace();
		}
		
		ContractDTO c = new ContractDTO();
		
		try {
			c = SCParser.parseContractInfo(bytes);
		} catch (ParseException pe) {
			System.out.println(pe.getMessage());
			return "Parse failed";
		}
		
		System.out.println(c);
		
		return c.toString();
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
			msg = "Failed";
			System.out.println("Failed to withdraw!");
			return msg;
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
			System.out.println("Failed to transfer!");
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
	
	public List<ContractRoomsInfo> findByContract_id(Long contract_id) {
		return contractRoomsInfoRepository.findByContract_id(contract_id);
	}
	
	private String returnErrorMessage(int code) {
		
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
		
	}

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
	
}
