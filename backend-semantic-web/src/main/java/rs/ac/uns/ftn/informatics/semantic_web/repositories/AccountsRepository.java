package rs.ac.uns.ftn.informatics.semantic_web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.informatics.semantic_web.model.Account;

public interface AccountsRepository extends JpaRepository<Account, Long> {

	public List<Account> findByAccount(String account);

}
