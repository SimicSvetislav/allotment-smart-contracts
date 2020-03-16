package rs.ac.uns.ftn.informatics.legal_tech.allotment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.services.ContractService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/web3j")
public class ContractsController {
	
	@Autowired
	private ContractService service;
	
	@GetMapping(path="/test")
	public ResponseEntity<String> test() {
		
		String msg = service.test();
		String msg2 = service.async();
		String msg3 = service.flowable();
		
		return new ResponseEntity<String>(msg + "\n" + msg2 + "\n" + msg3, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/deploy")
	public ResponseEntity<String> deploy() {
		
		String msg = service.deploy();
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/deployA")
	public ResponseEntity<String> deployA() {
		
		String msg = service.deployAllotment();
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/set")
	public ResponseEntity<String> set() {
		
		String msg = service.set();
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/setNum/{num}")
	public ResponseEntity<String> setNum(@PathVariable("num") Integer num) {
		
		String msg = service.setNum(num);
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/get")
	public ResponseEntity<String> get() {
		
		String msg = service.get();
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/getNum")
	public ResponseEntity<String> getNum() {
		
		String msg = service.getNum();
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/inc")
	public ResponseEntity<String> inc() {
		
		String msg = service.inc();
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
		
	}

}
