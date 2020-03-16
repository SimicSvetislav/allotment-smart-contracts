package rs.ac.uns.ftn.informatics.legal_tech.allotment.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.repositories.RepresentativeRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
    private RepresentativeRepository repository;
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String displayName) throws UsernameNotFoundException {     
        if(repository.findById(displayName) != null) {
        	return UserPrinciple.build(repository.findByDisplayName(displayName));
        } else {
        	return null;
        }
	}
}
