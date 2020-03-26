package rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Contract;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

	List<Contract> findByAccomodation_id(Long accomodation_id);
	
}
