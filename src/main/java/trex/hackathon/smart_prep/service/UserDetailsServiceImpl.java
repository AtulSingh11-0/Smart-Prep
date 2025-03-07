package trex.hackathon.smart_prep.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import trex.hackathon.smart_prep.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername ( String username ) throws UsernameNotFoundException {
		return userRepository.findByUsername(username)
				.orElseThrow( () -> new UsernameNotFoundException("User Not Found with username: " + username));
	}
}
