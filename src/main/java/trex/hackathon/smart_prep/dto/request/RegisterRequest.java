package trex.hackathon.smart_prep.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trex.hackathon.smart_prep.model.User;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

//	@NotBlank(message = "Name is required")
//	@Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
	private String name;

//	@NotBlank(message = "Username is required")
//	@Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
//	@Pattern(regexp = "^[a-zA-Z0-9._-]{4,20}$", message = "Username can only contain letters, numbers, dots, underscores and hyphens")
	private String username;

//	@NotBlank(message = "Email is required")
//	@Email(message = "Invalid email format")
	private String email;

//	@NotBlank(message = "Password is required")
//	@Size(min = 6, message = "Password must be at least 6 characters long")
	private String password;

	private User.Role role = User.Role.ROLE_STUDENT;
}