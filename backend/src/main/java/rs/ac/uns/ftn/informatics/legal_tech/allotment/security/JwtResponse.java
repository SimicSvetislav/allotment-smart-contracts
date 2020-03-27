package rs.ac.uns.ftn.informatics.legal_tech.allotment.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import lombok.Data;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.dto.RepresentativeDTO;

public @Data class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String displayName;
	private Collection<? extends GrantedAuthority> authorities;
	//private Integer idCompany;
	// private Long id;
	private RepresentativeDTO reprDto;

	public JwtResponse(String accessToken, String displayName, Collection<? extends GrantedAuthority> authorities, RepresentativeDTO reprDto) {
		this.token = accessToken;
		this.displayName = displayName;
		this.authorities = authorities;
		//this.idCompany = id;
		// this.id = id;
		this.reprDto = reprDto;
	}
	
	public String getDisplayName() {
		return displayName;
	}



	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}



	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}



	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }
}
