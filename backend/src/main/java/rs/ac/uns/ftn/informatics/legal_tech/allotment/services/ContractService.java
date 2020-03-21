package rs.ac.uns.ftn.informatics.legal_tech.allotment.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.bouncycastle.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.tx.Contract;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.exceptions.ContractCallException;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import io.reactivex.Flowable;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.SCParser;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.contracts.Allotment;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.contracts.HelloWorld;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.cto.ContractCTO;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.cto.ReservationCTO;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.dto.ReservationDTO;

@Service
public class ContractService {

	private Long PLATFORM_ACCOUNT = 10L;
	
	// private String PRIVATE_KEY="0x29b269ae87db6e792a0aaff8be7429e634aadf273266a7fb6589a34e0191ab68";
	// private String ADDRESS="0x11eccd64a00f8c0a1f81180ced0d538072138dbb";
	// Credentials credentials = Credentials.create(PRIVATE_KEY);
	
	@Autowired
	private Web3j web3j;
	
	@Autowired
	private RepresentativeService reprService;
	
	
	
	
	@SuppressWarnings("deprecation")
	public String deployAllotment(ContractCTO contract) {
		
		Allotment deployedContract = null;
		
		try {
			deployedContract = Allotment.deploy(web3j, reprService.getCredentials(PLATFORM_ACCOUNT), 
					DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT,
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
		}
		
		try {
			deployedContract._badOffSeasonMaxPenalty().send().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
				contractAddress, web3j, reprService.getCredentials(id),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);

		try {
			msg = contract.agencyAgreed().send().toString();
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
				contractAddress, web3j, reprService.getCredentials(userId),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);

		try {
			msg = contract.accomodationAgreed().send().toString();
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
	
	// Vraca balans naloga na koji se salje ether
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

		List<Long> hotels = new ArrayList<Long>();
		
		try {
			hotels = (List<Long>)contract.getHotels().send();
			msg = "Success";
		} catch (Exception e) {
			msg = "Failed";
			System.out.println("Failed to transfer!");
			return msg;
			//e.printStackTrace();
		}
		System.out.println("Returned: " + hotels);
		
		return hotels.toString();
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
		
		System.out.println("Returned: " + bytes.length);
		
		return msg;
	}

	public String getContractInfo(String address) {
		
		return null;
	}

	
}
