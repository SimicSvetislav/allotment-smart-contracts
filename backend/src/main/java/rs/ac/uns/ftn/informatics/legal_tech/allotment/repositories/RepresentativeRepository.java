package rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Representative;

@Repository
public interface RepresentativeRepository extends JpaRepository<Representative, String> {
	
	public Representative findByDisplayName(String displayName);

}
