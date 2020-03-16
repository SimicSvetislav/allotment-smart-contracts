package rs.ac.uns.ftn.informatics.legal_tech.allotment.services;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.tx.Contract;
import org.web3j.tx.gas.DefaultGasProvider;

import io.reactivex.Flowable;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.contracts.Allotment;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.contracts.HelloWorld;

@Service
public class ContractService {

	private String PRIVATE_KEY="0x6f4a0e654311122cb4f861d139d8287e3a1dd5c270bb5095318c124a9c16aead";
	private String ADDRESS="0x214ad8c6cfbdce19f00df1d41feb247ef734f9e8";
	Credentials credentials = Credentials.create(PRIVATE_KEY);
	
	@Autowired
	Web3j web3j;
	
	public String test() {
		
		Web3ClientVersion web3ClientVersion = null;
		try {
			web3ClientVersion = web3j.web3ClientVersion().send();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String clientVersion = web3ClientVersion.getWeb3ClientVersion();
		System.out.println(clientVersion);
		
		return clientVersion;
	}

	public String async() {

		CompletableFuture<Web3ClientVersion> future = web3j.web3ClientVersion().sendAsync();
		Web3ClientVersion web3ClientVersion = null;
		try {
			web3ClientVersion = future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		String clientVersion = web3ClientVersion.getWeb3ClientVersion();
		System.out.println(clientVersion);
		
		return clientVersion;
	}

	public String flowable() {
		
		Flowable<Web3ClientVersion> flowable = web3j.web3ClientVersion().flowable();
		flowable.subscribe((web3ClientVersion) -> {
		    String clientVersion = web3ClientVersion.getWeb3ClientVersion();
		    System.out.println(clientVersion);
		});
		
		return "OK";
	}

	@SuppressWarnings("deprecation")
	public String deploy() {
		
		HelloWorld contract = null;
		
		try {
			contract = HelloWorld.deploy(web3j, credentials, DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT,
					"Init message", new BigInteger("1")).sendAsync().get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ADDRESS = contract.getContractAddress();
		
		return ADDRESS;
	}
	
	public String set() {
		
		@SuppressWarnings("deprecation")
		HelloWorld contract = HelloWorld.load(
		        ADDRESS, web3j, credentials,
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
		
		TransactionReceipt receipt = null;
		try {
			receipt = contract.setMessage("Hello World!").send();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(receipt.getTransactionHash());
		
		return receipt.getTransactionHash();
	}

	public String setNum(Integer num) {
		@SuppressWarnings("deprecation")
		HelloWorld contract = HelloWorld.load(
		        ADDRESS, web3j, credentials,
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
		
		TransactionReceipt receipt = null;
		try {
			receipt = contract.setNumber(BigInteger.valueOf(num)).send();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(receipt.getTransactionHash());
		
		return receipt.getTransactionHash();
	}

	public String get() {
		@SuppressWarnings("deprecation")
		HelloWorld contract = HelloWorld.load(
		        ADDRESS, web3j, credentials,
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
		
		String msg = null;
		try {
			msg = contract.getMessage().send();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Message: " + msg);
		
		return msg;
	}

	public String getNum() {
		HelloWorld contract = (HelloWorld)getContract(ADDRESS);
		
		String num = null;
		try {
			num = contract.number().send().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Number: " + num);
		
		return num;
	}

	public String inc() {
		
		HelloWorld contract = (HelloWorld)getContract(ADDRESS);
		Long number = null;
		
		BigInteger gasLimit = contract.GAS_LIMIT;
		System.out.println(gasLimit);
		
		try {
			// RemoteFunctionCall<BigInteger> call = contract.inc(BigInteger.valueOf(5L));
			RemoteFunctionCall<BigInteger> call = contract.inc();
			BigInteger bi = call.send();
			number = bi.longValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Number: " + number);
		
		return number.toString();
	}
	
	@SuppressWarnings("deprecation")
	private Contract getContract(String address) {
		return HelloWorld.load(
		        address, web3j, credentials,
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
		
	}
	
	@SuppressWarnings("deprecation")
	public String deployAllotment() {
		String msg = "";
		
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
		roomsInfo.add(bi(100));
		roomsInfo.add(bi(25));
		roomsInfo.add(bi(15));
		roomsInfo.add(bi(15));
		
		List<BigInteger> periods = new ArrayList<BigInteger>();
		periods.add(bi(10));
		periods.add(bi(7));
		
		String courtName = "Osnovni sud";
		String courtLocation = "Novi Sad";
		
		try {
			contract = Allotment.deploy(web3j, credentials, 
					DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT,
					"0x561bB571E927FF9de50bD60b036224460F59bFD7", 
					"0x068990AB686a17731cb2432baC22F36fc9d6456a",
					bi(1589981726), bi(1595252126), 
					hotels, prices, someConstrains, roomsInfo, periods,
					false, bi(300), bi(5), bi(10),
					courtName, courtLocation
					).send();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ADDRESS = contract.getContractAddress();
		
		try {
			msg = contract._badOffSeasonMaxPenalty().send().toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return msg;
	}
	
	private BigInteger bi(Integer i) {
		return BigInteger.valueOf(i);
	}

}
