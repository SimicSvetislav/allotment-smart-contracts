package rs.ac.uns.ftn.informatics.semantic_web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.informatics.semantic_web.model.Representative;

@Repository
public interface RepresentativeRepository extends JpaRepository<Representative, Long> {
	
	public Representative findByDisplayName(String displayName);

}
