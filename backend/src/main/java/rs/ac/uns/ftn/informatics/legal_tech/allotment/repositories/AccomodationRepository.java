package rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Accomodation;

@Repository
public interface AccomodationRepository extends JpaRepository<Accomodation, Long> {

	public Accomodation findByAccount(String account);
	
}
