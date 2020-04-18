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

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Hotel;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.services.HotelService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/hotel")
public class HotelController {

	@Autowired
	private HotelService service;
	
	@GetMapping(path="/{id}")
	public ResponseEntity<List<Hotel>> getHotels(@PathVariable("id") Long id) {
		
		List<Hotel> hotels = service.findHotelsByAcc(id);
		
		return new ResponseEntity<List<Hotel>>(hotels, HttpStatus.OK);
		
	}
	
	
}
