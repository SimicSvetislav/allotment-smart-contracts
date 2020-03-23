package rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Account;

public interface AccountsRepository extends JpaRepository<Account, Long> {

	Account findByAccount(String account);

}
