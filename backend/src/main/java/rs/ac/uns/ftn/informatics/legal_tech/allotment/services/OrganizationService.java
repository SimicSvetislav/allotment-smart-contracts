package rs.ac.uns.ftn.informatics.legal_tech.allotment.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Organization;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.OrganizationRepository;

@Service
public class OrganizationService {

	@Autowired
	private OrganizationRepository repository;
	
	public Organization findOneById(Long id) {
		return repository.findById(id).get();
	}
	
}
