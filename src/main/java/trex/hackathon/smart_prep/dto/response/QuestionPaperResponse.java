package trex.hackathon.smart_prep.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trex.hackathon.smart_prep.model.QuestionPaper;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionPaperResponse {

	private Long id;
	private String title;
	private String description;
	private Long questionBankId;
	private String questionBankName;
	private Long creatorId;
	private String creatorName;
	private Integer duration;
	private QuestionPaper.DifficultyLevel difficultyLevel;
	private Integer totalQuestions;
	private Integer totalMarks;
	private Integer passingMarks;
	private boolean published;
//	private int questionsCount;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static QuestionPaperResponse fromQuestionPaper(QuestionPaper questionPaper) {
		QuestionPaperResponseBuilder builder = QuestionPaperResponse.builder()
				.id(questionPaper.getId())
				.title(questionPaper.getTitle())
				.description(questionPaper.getDescription())
				.questionBankId(questionPaper.getQuestionBank().getId())
				.questionBankName(questionPaper.getQuestionBank().getName())
				.creatorId(questionPaper.getCreator().getId())
				.creatorName(questionPaper.getCreator().getName())
				.duration(questionPaper.getDuration())
				.difficultyLevel(questionPaper.getDifficultyLevel())
				.totalQuestions(questionPaper.getTotalQuestions())
				.totalMarks(questionPaper.getTotalMarks())
				.passingMarks(questionPaper.getPassingMarks())
				.published(questionPaper.isPublished())
//				.questionsCount(includeQuestions ? questionPaper.getQuestions().size() : 0)
				.createdAt(questionPaper.getCreatedAt())
				.updatedAt(questionPaper.getUpdatedAt());

		return builder.build();
	}
}
