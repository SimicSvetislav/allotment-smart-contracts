package rs.ac.uns.ftn.informatics.legal_tech.allotment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.services.TestService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/web3j")
public class TestController {

	@Autowired
	private TestService testService;
	
	@GetMapping(path="/test")
	public ResponseEntity<String> test() {
		
		String msg = testService.test();
		String msg2 = testService.async();
		String msg3 = testService.flowable();
		
		return new ResponseEntity<String>(msg + "\n" + msg2 + "\n" + msg3, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/deploy")
	public ResponseEntity<String> deploy() {
		
		String msg = testService.deploy();
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/set")
	public ResponseEntity<String> set() {
		
		String msg = testService.set();
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/setNum/{num}")
	public ResponseEntity<String> setNum(@PathVariable("num") Integer num) {
		
		String msg = testService.setNum(num);
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/get")
	public ResponseEntity<String> get() {
		
		String msg = testService.get();
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/getNum")
	public ResponseEntity<String> getNum() {
		
		String msg = testService.getNum();
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/inc")
	public ResponseEntity<String> inc() {
		
		String msg = testService.inc();
		
		return new ResponseEntity<String>(msg, HttpStatus.OK);
		
	}
	
}
