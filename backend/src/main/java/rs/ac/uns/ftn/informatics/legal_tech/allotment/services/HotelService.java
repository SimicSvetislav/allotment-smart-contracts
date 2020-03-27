package rs.ac.uns.ftn.informatics.legal_tech.allotment.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Hotel;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.RoomsInfo;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.HotelRepository;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.RoomsInfoRepository;

@Service
public class HotelService {

	@Autowired
	private HotelRepository repo;
	
	@Autowired
	private RoomsInfoRepository riRepo;

	public Hotel findById(Long id) {
		Optional<Hotel> opt = repo.findById(id);
		return opt.get();
	}
	
	public List<RoomsInfo> getRoomsInfo(Long hotelId) {
		
		List<RoomsInfo> roomsInfo = riRepo.findByHotel_id(hotelId);
		
		return roomsInfo;
		
	}
	
	public List<Hotel> findHotelsByAcc(Long id) {
		return repo.findByOrg_id(id);
	}
	
}
