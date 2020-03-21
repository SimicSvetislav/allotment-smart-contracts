package rs.ac.uns.ftn.informatics.legal_tech.allotment.services;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.tx.Contract;
import org.web3j.tx.gas.DefaultGasProvider;

import io.reactivex.Flowable;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.contracts.HelloWorld;

@Service
public class TestService {

	private Long PLATFORM_ACCOUNT = 10L;
	
	@Autowired
	private Web3j web3j;
	
	@Autowired
	private RepresentativeService reprService;
	
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
		
		// HelloWorld contract = null;
		
		try {
			HelloWorld.deploy(web3j, reprService.getCredentials(PLATFORM_ACCOUNT), DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT,
					"Init message", new BigInteger("1")).sendAsync().get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// ADDRESS = contract.getContractAddress();
		
		return "OK";
	}
	
	public String set() {
		
		String address = "ADD ADDRESS";
		
		@SuppressWarnings("deprecation")
		HelloWorld contract = HelloWorld.load(
				address, web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
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
		
		String address = "ADD ADDRESS";
		
		@SuppressWarnings("deprecation")
		HelloWorld contract = HelloWorld.load(
		        address, web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
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
		
		String address = "ADD ADDRESS";
		
		@SuppressWarnings("deprecation")
		HelloWorld contract = HelloWorld.load(
		        address, web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
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
		
		String address = "ADD ADDRESS";
		
		HelloWorld contract = (HelloWorld)getContract(address);
		
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
		
		String address = "ADD ADDRESS";
		
		HelloWorld contract = (HelloWorld)getContract(address);
		Long number = null;
		
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
		        address, web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
		        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
		
	}
	
}
