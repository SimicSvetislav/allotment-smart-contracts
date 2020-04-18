package rs.ac.uns.ftn.informatics.legal_tech.allotment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.DefaultGasProvider;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.contracts.Allotment;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Contract;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.ContractRepository;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.services.RepresentativeService;

@Component
public class ScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

	@Autowired
	private ContractRepository contractRepository;
	
	@Autowired
	private RepresentativeService reprService;
	
	@Autowired
	private Web3j web3j;
	
	private Long PLATFORM_ACCOUNT = 10L;
	
	@Async
	@Scheduled(fixedRate = 3*60*1000)
	public void logTime() {
		log.info("The time is now {}", dateFormat.format(new Date()));
	}
	
	// Method sample
	@Async
	@Scheduled(fixedRate = 3*60*1000, initialDelay = 10000)
	public void checkOffseasonSample() {
		System.out.println("Fake checkOffseasonSample() triggered");
		
		List<Contract> allContracts = contractRepository.findAll();
		
		Date today = new Date();
		
		for (Contract c: allContracts) {
			@SuppressWarnings("deprecation")
			Allotment contract = Allotment.load(
					c.getAddress(), web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
			        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
			
			if (today.after(c.getStartDate())) {
				try {
					contract.checkOffseason().send();
				} catch (Exception e) {
					log.info("Function reverted or error occured while calling checkOffseason() for contract with id=" + c.getId());
					// e.printStackTrace();
				}
			}
		}
		
	}
	
	// Real method
	// Check for contracts where preseason has ended
	@Async
	@Scheduled(fixedRate = 24*60*60*1000, initialDelay = 10000)
	public void checkOffseason() {
		System.out.println("Real checkOffseason() triggered");
		
		List<Contract> allContracts = contractRepository.findAll();
		
		Date today = new Date();
		
		for (Contract c: allContracts) {
			@SuppressWarnings("deprecation")
			Allotment contract = Allotment.load(
					c.getAddress(), web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
			        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
			
			if (today.after(c.getStartDate())) {
				try {
					contract.checkOffseason().send();
				} catch (Exception e) {
					log.info("Function reverted or error occured while calling checkOffseason() for contract with id=" + c.getId());
					// e.printStackTrace();
				}
			}
		}
	}
	
	// Method sample
	@Async
	@Scheduled(fixedRate = 10*1000, initialDelay = 10000)
	public void checkExpirationSample() {
		System.out.println("Fake checkExpirationSample() triggered");
		
		List<Contract> allContracts = contractRepository.findAll();
		
		Date today = new Date();
		
		for (Contract c: allContracts) {
			@SuppressWarnings("deprecation")
			Allotment contract = Allotment.load(
					c.getAddress(), web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
			        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
			
			if (today.after(c.getEndDate())) {
				try {
					contract.finalizeContract().send();
				} catch (Exception e) {
					log.info("Function reverted or error occured while calling finalizeContract() for contract with id=" + c.getId());
					// e.printStackTrace();
				}
			}
		}
	}
	
	// Real method
	// Check for expired contracts
	@Async
	@Scheduled(fixedRate = 24*60*60*1000, initialDelay = 10000)
	public void checkExpiration() {
		System.out.println("Real checkExpiration() triggered");
		
List<Contract> allContracts = contractRepository.findAll();
		
		Date today = new Date();
		
		for (Contract c: allContracts) {
			@SuppressWarnings("deprecation")
			Allotment contract = Allotment.load(
					c.getAddress(), web3j, reprService.getCredentials(PLATFORM_ACCOUNT),
			        DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
			
			if (today.after(c.getEndDate())) {
				try {
					contract.finalizeContract().send();
				} catch (Exception e) {
					log.info("Function reverted or error occured while calling finalizeContract() for contract with id=" + c.getId());
					// e.printStackTrace();
				}
			}
		}
	}
}
