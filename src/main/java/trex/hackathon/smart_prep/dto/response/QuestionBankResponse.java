package trex.hackathon.smart_prep.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trex.hackathon.smart_prep.model.QuestionBank;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionBankResponse {

	private Long id;
	private String name;
	private String description;
	private Long creatorId;
	private String creatorName;
	private boolean isPublic;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static QuestionBankResponse fromQuestionBank(QuestionBank questionBank) {
		return QuestionBankResponse.builder()
				.id(questionBank.getId())
				.name(questionBank.getName())
				.description(questionBank.getDescription())
				.creatorId(questionBank.getCreator().getId())
				.creatorName(questionBank.getCreator().getName())
				.isPublic(questionBank.isPublic())
				.createdAt(questionBank.getCreatedAt())
				.updatedAt(questionBank.getUpdatedAt())
				.build();
	}
}