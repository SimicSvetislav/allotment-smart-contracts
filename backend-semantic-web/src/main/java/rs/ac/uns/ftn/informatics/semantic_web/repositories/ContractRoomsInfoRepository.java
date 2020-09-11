package rs.ac.uns.ftn.informatics.semantic_web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.informatics.semantic_web.model.ContractRoomsInfo;

@Repository
public interface ContractRoomsInfoRepository extends JpaRepository<ContractRoomsInfo, Long> {

	public List<ContractRoomsInfo> findByContract_id(Long contract_id);

	public void deleteByContract_id(Long contract_id);
	
}
