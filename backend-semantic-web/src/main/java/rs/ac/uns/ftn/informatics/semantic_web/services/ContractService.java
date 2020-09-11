package rs.ac.uns.ftn.informatics.semantic_web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.informatics.semantic_web.model.Contract;
import rs.ac.uns.ftn.informatics.semantic_web.repositories.ContractRepository;

@Service
public class ContractService {
	
	@Autowired
	private ContractRepository repo;
	
	public Contract findById(Long id) {
		Contract c = repo.findById(id).get();
		return c;
	}

}
