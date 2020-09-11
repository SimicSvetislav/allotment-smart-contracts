package rs.ac.uns.ftn.informatics.semantic_web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.informatics.semantic_web.model.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

}
