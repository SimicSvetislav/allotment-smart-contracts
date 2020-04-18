package rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Contract;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

	List<Contract> findByAccomodation_id(Long accomodation_id);
	
	Contract findByAddress(String address);
	
	@Query("SELECT ctr FROM Contract ctr WHERE ctr.accomodation.id=:org_id or ctr.agency.id=:org_id")
	List<Contract> findContractsByOrg(Long org_id);
	
	@Query("SELECT ctr FROM Contract ctr WHERE ctr.status like %:status% and (ctr.accomodation.id=:org_id or ctr.agency.id=:org_id)")
	List<Contract> findContractsByOrgAndStatus(Long org_id, String status);
	
}
