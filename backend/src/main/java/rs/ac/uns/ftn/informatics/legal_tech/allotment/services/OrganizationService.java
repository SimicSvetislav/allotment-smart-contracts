package rs.ac.uns.ftn.informatics.legal_tech.allotment.services;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Organization;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.OrganizationRepository;

@Service
public class OrganizationService {

	@Autowired
	private OrganizationRepository repository;
	
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
	
}
