package rs.ac.uns.ftn.informatics.legal_tech.allotment.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Account;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Representative;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.AccountsRepository;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.RepresentativeRepository;

@Service
public class RepresentativeService {
	
	@Autowired
	private RepresentativeRepository repository;
	
	@Autowired
	private AccountsRepository accountsRepository;
	
	public Representative findByDisplayName(String displayName) {
		return repository.findByDisplayName(displayName);
	}

	public void save(Representative repr) {
		repository.save(repr);
	}

	public Credentials getCredentials(Long userId) {
		
		repository.findById(userId);
		
		Account account = accountsRepository.findById(userId).get();
		
		return Credentials.create(account.getPrivateKey());
		
	}

	public String getAccountAddress(Long id) {
		
		Account account = accountsRepository.findById(id).get();
		
		String address = account.getAccount(); 
		
		return address;
		
	}

}
