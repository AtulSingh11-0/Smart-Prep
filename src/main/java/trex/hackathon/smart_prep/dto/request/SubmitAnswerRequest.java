package trex.hackathon.smart_prep.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmitAnswerRequest {
	private Long questionId;
	private Integer selectedOptionIndex;  // For MCQ
	private String textAnswer;            // For SAQ
}