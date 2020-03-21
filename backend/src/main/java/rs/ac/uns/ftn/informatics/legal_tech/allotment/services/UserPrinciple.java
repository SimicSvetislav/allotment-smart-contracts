package rs.ac.uns.ftn.informatics.legal_tech.allotment.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Representative;

public class UserPrinciple implements UserDetails {

	private static final long serialVersionUID = 1L;

	private Long id;

    private String displayName;
    
    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public UserPrinciple(Long id, String email, String displayName, String password,
			    		Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserPrinciple build(Object obj) {
    	
    	List<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
    	
    	//if(obj instanceof Representative) {
    		auth.add(new SimpleGrantedAuthority("ROLE_REPRESENTATIVE"));
    		 return new UserPrinciple(
    	                ((Representative) obj).getId(),
    	                ((Representative) obj).getEmail(),
    	                ((Representative) obj).getDisplayName(),
    	                ((Representative) obj).getPassword(),
    	                auth
    	        );
    	//}
    }

    /*public Long getId() {
        return id;
    }*/

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getUsername() {
        return displayName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        UserPrinciple user = (UserPrinciple) o;
        return Objects.equals(id, user.id);
    }
	
}
