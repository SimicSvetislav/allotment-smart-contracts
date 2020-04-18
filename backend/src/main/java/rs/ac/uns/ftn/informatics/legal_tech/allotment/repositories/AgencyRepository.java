package rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Accomodation;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Agency;

@Repository
public interface AgencyRepository extends JpaRepository<Agency, Long> {

	@Query("SELECT ag FROM Agency ag WHERE ag.account like %:address%")
	public Agency findByAddress(String address);
	
	public Accomodation findByAccount(String account);
	
}
