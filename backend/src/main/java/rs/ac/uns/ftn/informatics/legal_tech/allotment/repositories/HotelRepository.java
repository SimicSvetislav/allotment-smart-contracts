package rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

}
