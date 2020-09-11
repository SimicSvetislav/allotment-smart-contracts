package rs.ac.uns.ftn.informatics.semantic_web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.informatics.semantic_web.model.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

	public List<Hotel> findByOrg_id(Long id); 
	
}
