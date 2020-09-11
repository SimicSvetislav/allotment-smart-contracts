package rs.ac.uns.ftn.informatics.semantic_web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.informatics.semantic_web.model.RoomsInfo;

@Repository
public interface RoomsInfoRepository extends JpaRepository<RoomsInfo, Long> {

	List<RoomsInfo> findByHotel_id(Long hotel_id);
	
}
