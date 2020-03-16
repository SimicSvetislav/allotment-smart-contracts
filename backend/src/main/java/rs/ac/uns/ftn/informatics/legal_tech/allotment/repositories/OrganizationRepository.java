package rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, String> {

}
