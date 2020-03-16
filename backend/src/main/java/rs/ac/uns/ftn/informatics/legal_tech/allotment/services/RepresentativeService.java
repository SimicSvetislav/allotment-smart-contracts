package rs.ac.uns.ftn.informatics.legal_tech.allotment.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Representative;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.RepresentativeRepository;

@Service
public class RepresentativeService {
	
	@Autowired
	private RepresentativeRepository repository;
	
	public Representative findByDisplayName(String displayName) {
		return repository.findByDisplayName(displayName);
	}

	public void save(Representative repr) {
		repository.save(repr);
	}

}
