package rs.ac.uns.ftn.informatics.legal_tech.allotment.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Accomodation;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Agency;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Organization;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.AccomodationRepository;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.AgencyRepository;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.OrganizationRepository;

@Service
public class OrganizationService {

	@Autowired
	private OrganizationRepository repository;
	
	@Autowired
	private AgencyRepository agRepository;
	
	@Autowired
	private AccomodationRepository accRepository;
	
	public Organization findOneById(Long id) {
		
		Organization org = null;
		try {
			org = repository.findById(id).get();
		} catch (NoSuchElementException e) {
			System.out.println("Can't find organization");
			e.getMessage();
		}
		
		return org;
	}

	public Organization save(Organization org) {

		return repository.save(org);
		
	}
	
	public List<Agency> getAgencies() {

		return agRepository.findAll();
		
	}
	
	public List<Accomodation> getAccomodations() {

		return accRepository.findAll();
		
	}
	
}
