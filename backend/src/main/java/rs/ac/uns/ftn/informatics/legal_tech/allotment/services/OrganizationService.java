package rs.ac.uns.ftn.informatics.legal_tech.allotment.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Organization;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.OrganizationRepository;

@Service
public class OrganizationService {

	@Autowired
	private OrganizationRepository repository;
	
	public Optional<Organization> findOneByNetworkAddress(String address) {
		return repository.findById(address);
	}
	
}
