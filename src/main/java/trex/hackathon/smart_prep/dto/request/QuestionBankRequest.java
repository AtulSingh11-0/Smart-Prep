package trex.hackathon.smart_prep.dto.request;

//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionBankRequest {

//	@NotBlank(message = "Question bank name is required")
//	@Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
	private String name;

	private String subject;

	private String description;

	private boolean isPublic;
}