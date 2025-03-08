package trex.hackathon.smart_prep.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trex.hackathon.smart_prep.model.Question;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {
	private Long id;
	private String questionText;
	private Question.QuestionType questionType;
	private Question.DifficultyLevel difficultyLevel;
	private Integer marks;
	private Integer expectedTimeSeconds;
	private List<String> options;
	private Integer correctOptionIndex;
	private String modelAnswer;
	private Long questionPaperId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

public static QuestionResponse fromQuestion ( Question question) {
		return QuestionResponse.builder()
				.id(question.getId())
				.questionText(question.getQuestionText())
				.questionType(question.getQuestionType())
				.difficultyLevel(question.getDifficultyLevel())
				.marks(question.getMarks())
				.expectedTimeSeconds(question.getExpectedTimeSeconds())
				.options(question.getOptions())
				.correctOptionIndex(question.getCorrectOptionIndex())
				.modelAnswer(question.getModelAnswer())
				.questionPaperId(question.getQuestionPaper().getId())
				.createdAt(question.getCreatedAt())
				.updatedAt(question.getUpdatedAt())
				.build();
}
}
