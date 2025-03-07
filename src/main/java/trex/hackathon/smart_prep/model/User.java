package trex.hackathon.smart_prep.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@Column(name = "profile_picture")
	private String profilePictureUrl;

	@Column
	private String bio;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "last_login")
	private LocalDateTime lastLogin;

	@Column(name = "account_locked")
	private boolean accountLocked;

	@Column(name = "account_expired")
	private boolean accountExpired;

	@Column(name = "credentials_expired")
	private boolean credentialsExpired;

	@Column(name = "account_enabled")
	private boolean enabled = true;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	@Override
	public Collection< ? extends GrantedAuthority > getAuthorities () {
		return List.of(new SimpleGrantedAuthority(this.role.name()));
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired () {
		return !this.isAccountExpired();
	}

	@Override
	public boolean isAccountNonLocked () {
		return !this.isAccountLocked();
	}

	@Override
	public boolean isCredentialsNonExpired () {
		return !this.isCredentialsExpired();
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	public enum Role {
		ROLE_STUDENT, ROLE_TEACHER, ROLE_ADMIN
	}
}
