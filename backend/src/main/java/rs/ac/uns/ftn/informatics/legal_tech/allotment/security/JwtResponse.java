package rs.ac.uns.ftn.informatics.legal_tech.allotment.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String displayName;
	private Collection<? extends GrantedAuthority> authorities;
	//private Integer idCompany;
	private Long id;

	public JwtResponse(String accessToken, String displayName, Collection<? extends GrantedAuthority> authorities, Long id) {
		this.token = accessToken;
		this.displayName = displayName;
		this.authorities = authorities;
		//this.idCompany = id;
		this.id = id;
	}
	
	

    public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	/*public Integer getIdCompany() {
		return idCompany;
	}
	public void setIdCompany(Integer idCompany) {
		this.idCompany = idCompany;
	}*/



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
