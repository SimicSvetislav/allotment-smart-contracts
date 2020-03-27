package rs.ac.uns.ftn.informatics.legal_tech.allotment.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Accomodation;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Agency;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Organization;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.services.OrganizationService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/org")
public class OrganizationController {

	@Autowired
	private OrganizationService service;
	
	@GetMapping(path="/agencies")
	public ResponseEntity<List<Agency>> getAgencies() {
		
		List<Agency> orgs = service.getAgencies();
		
		return new ResponseEntity<List<Agency>>(orgs, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/accomodations")
	public ResponseEntity<List<Accomodation>> getAccomodations() {
		
		List<Accomodation> orgs = service.getAccomodations();
		
		return new ResponseEntity<List<Accomodation>>(orgs, HttpStatus.OK);
		
	}
	
}
