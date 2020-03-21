package rs.ac.uns.ftn.informatics.legal_tech.allotment.controllers;

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

import rs.ac.uns.ftn.informatics.legal_tech.allotment.cto.ContractCTO;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.cto.ReservationCTO;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.dto.ContractAddressPair;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.dto.TransferPair;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.services.ContractService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/web3j")
public class ContractsController {
	
	@Autowired
	private ContractService service;
	
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
		
	}
	
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
	public ResponseEntity<Double[]> balanceUser() {
		
		Double[] balances = new Double[10];
		
		for (int i=0; i<10; i++) {
			Double balance = service.getEtherBalanceAccount(new Long(i+1));
			balances[i] = balance;
		}
		
		return new ResponseEntity<Double[]>(balances, HttpStatus.OK);
		
	}
	
	@PostMapping(path="/ress/{beds}")
	public ResponseEntity<String> getRess(@RequestBody ContractAddressPair pair, @PathVariable("beds") Integer beds) {
		
		
		String retVal = service.getRess(beds, pair.getContractAddress());

		
		return new ResponseEntity<String>(retVal, HttpStatus.OK);
		
	}
	
	@PostMapping(path="/ressAll")
	public ResponseEntity<String> getRessAll(@RequestBody ContractAddressPair pair) {
		
		
		String retVal = service.getRessAll(pair.getContractAddress());

		
		return new ResponseEntity<String>(retVal, HttpStatus.OK);
		
	}
	
	@PostMapping(path="/reserve/{repr}/{address}")
	public ResponseEntity<String> reserve(
			@RequestBody ReservationCTO res, 
			@PathVariable("address") String address,
			@PathVariable("repr") Long repr) {
		
		
		String retVal = service.reserve(address, repr, res);

		
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
	
	@PostMapping(path="/deployA")
	public ResponseEntity<String> deployParameters(
			@RequestBody ContractCTO contract) {
		
		
		String retVal = service.deployAllotment(contract);

		
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
		
		
		String retVal = service.getContractInfo(address);

		
		return new ResponseEntity<String>(retVal, HttpStatus.OK);
		
	}
	
}
