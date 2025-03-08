package trex.hackathon.smart_prep.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trex.hackathon.smart_prep.model.User;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
	private String token;
	private UserDto user;

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class UserDto {
		private Long id;
		private String name;
		private String username;
		private String email;
		private String role;
		private String profilePictureUrl;
		private String bio;

		public static UserDto fromUser(User user) {
			return UserDto.builder()
					.id(user.getId())
					.name(user.getName())
					.username(user.getUsername())
					.email(user.getEmail())
					.role(user.getRole().name())
					.profilePictureUrl(user.getProfilePictureUrl())
					.bio(user.getBio())
					.build();
		}
	}
}