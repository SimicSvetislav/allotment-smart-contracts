package rs.ac.uns.ftn.informatics.legal_tech.allotment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.services.RepresentativeService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/repr")
public class RepresentativeController {
	
	@Autowired
	private RepresentativeService service;
	
	@GetMapping(path="/test")
	public ResponseEntity<String> test() {
		return new ResponseEntity<String>("Proslo je dobro", HttpStatus.OK);
	}

}
