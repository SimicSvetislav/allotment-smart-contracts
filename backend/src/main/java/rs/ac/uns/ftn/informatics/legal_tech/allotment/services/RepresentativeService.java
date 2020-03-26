package rs.ac.uns.ftn.informatics.legal_tech.allotment.services;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Account;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Organization;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Representative;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.AccountsRepository;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.RepresentativeRepository;

@Service
public class RepresentativeService {
	
	@Autowired
	private RepresentativeRepository repository;
	
	@Autowired
	private AccountsRepository accountsRepository;
	
	@Autowired
	private OrganizationService orgService;
	
	public Representative findByDisplayName(String displayName) {
		return repository.findByDisplayName(displayName);
	}

	public void save(Representative repr) {
		repository.save(repr);
	}

	public Credentials getCredentials(Long userId) {
				
		Account account = accountsRepository.findById(userId).get();
		
		return Credentials.create(account.getPrivateKey());
		
	}
	
	public Credentials getCredentialsFromOrg(Long userId) {
		
		Representative repr = null;
		try {
			repr = repository.findById(userId).get();
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			System.out.println("Can't find representative");
			return null;
		}
		
		Organization org = orgService.findOneById(repr.getRepresenting().getId());
		
		Account account = null;
		try {
			account = accountsRepository.findByAccount(org.getAccount());
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			System.out.println("Can't get account");
			return null;
		}
		
		return Credentials.create(account.getPrivateKey());
		
	}

	public String getAccountAddress(Long id) {
		
		Account account = accountsRepository.findById(id).get();
		
		String address = account.getAccount(); 
		
		return address;
		
	}

	public Representative getById(Long id) {
		return repository.findById(id).get();
	}

}
