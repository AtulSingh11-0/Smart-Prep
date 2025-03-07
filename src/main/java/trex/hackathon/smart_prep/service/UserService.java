package trex.hackathon.smart_prep.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import trex.hackathon.smart_prep.dto.response.AuthResponse.UserDto;
import trex.hackathon.smart_prep.exception.UserNotFoundException;
import trex.hackathon.smart_prep.model.User;
import trex.hackathon.smart_prep.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public UserDto getUserById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
		return UserDto.fromUser(user);
	}

	public Page<UserDto> getAllUsers(int page, int size) {
		Page<User> users = userRepository.findAll(PageRequest.of(page, size));
		return users.map(UserDto::fromUser);
	}
}