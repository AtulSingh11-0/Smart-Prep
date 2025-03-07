package trex.hackathon.smart_prep.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import trex.hackathon.smart_prep.dto.request.LoginRequest;
import trex.hackathon.smart_prep.dto.request.RegisterRequest;
import trex.hackathon.smart_prep.dto.response.AuthResponse;
import trex.hackathon.smart_prep.dto.response.AuthResponse.UserDto;
import trex.hackathon.smart_prep.exception.UserAlreadyExistsException;
import trex.hackathon.smart_prep.model.User;
import trex.hackathon.smart_prep.repository.UserRepository;
import trex.hackathon.smart_prep.security.JwtTokenUtil;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenUtil jwtTokenUtil;
	private final AuthenticationManager authenticationManager;

	public AuthResponse register(RegisterRequest request) {
		// Check if username or email already exists
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new UserAlreadyExistsException("Username already taken");
		}

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new UserAlreadyExistsException("Email already registered");
		}

		// Create and save new user
		User user = User.builder()
				.name(request.getName())
				.username(request.getUsername())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(request.getRole())
				.createdAt(LocalDateTime.now())
				.enabled(true)
				.build();

		User savedUser = userRepository.save(user);

		// Generate JWT token
		String token = jwtTokenUtil.generateToken(savedUser);

		return AuthResponse.builder()
				.user(UserDto.fromUser(savedUser))
				.token(token)
				.build();
	}

	public AuthResponse login(LoginRequest request) {
		// Authenticate user
		String username = request.getUsernameOrEmail();

		// Check if input is email
		if (username.contains("@")) {
			User user = userRepository.findByEmail(username)
					.orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
			username = user.getUsername();
		}

		// Authenticate
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, request.getPassword())
		);

		// Get user and update last login time
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

		user.setLastLogin(LocalDateTime.now());
		userRepository.save(user);

		// Generate token
		String token = jwtTokenUtil.generateToken(user);

		return AuthResponse.builder()
				.user(UserDto.fromUser(user))
				.token(token)
				.build();
	}
}