package rs.ac.uns.ftn.informatics.legal_tech.allotment.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Account;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.AccountsRepository;

@Service
public class AccountsService {

	@Autowired
	private AccountsRepository repo;
	
	public Account save(Account entity) {
	
		return repo.save(entity);
		
	}
	
}
