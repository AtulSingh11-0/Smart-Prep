package trex.hackathon.smart_prep.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trex.hackathon.smart_prep.model.QuestionPaper;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvailableQuestionPaperResponse {
	private Long id;
	private String title;
	private String description;
	private String questionBankName;
	private String creatorName;
	private Integer duration;
	private Integer totalQuestions;
	private QuestionPaper.DifficultyLevel difficultyLevel;
	private Integer totalMarks;
	private Integer passingMarks;
	private Integer attemptsMade;
	private Boolean hasPendingAttempt;

	public static AvailableQuestionPaperResponse fromQuestionPaper(
			QuestionPaper questionPaper, Integer attemptsMade, Boolean hasPendingAttempt) {

		return AvailableQuestionPaperResponse.builder()
				.id(questionPaper.getId())
				.title(questionPaper.getTitle())
				.description(questionPaper.getDescription())
				.questionBankName(questionPaper.getQuestionBank().getName())
				.creatorName(questionPaper.getCreator().getName())
				.duration(questionPaper.getDuration())
				.totalQuestions(questionPaper.getTotalQuestions())
				.difficultyLevel(questionPaper.getDifficultyLevel())
				.totalMarks(questionPaper.getTotalMarks())
				.passingMarks(questionPaper.getPassingMarks())
				.attemptsMade(attemptsMade)
				.hasPendingAttempt(hasPendingAttempt)
				.build();
	}
}