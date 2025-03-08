package trex.hackathon.smart_prep.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trex.hackathon.smart_prep.model.QuizAttempt;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizAttemptResponse {
	private Long id;
	private Long questionPaperId;
	private String questionPaperTitle;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private boolean completed;
	private Integer score;
	private Integer totalMarks;
	private Double percentage;
	private Boolean passed;
	private Long remainingTime;  // in seconds
	private List<StudentAnswerResponse> answers = new ArrayList<>();

	public static QuizAttemptResponse fromQuizAttempt(
			QuizAttempt quizAttempt, boolean includeAnswers, List<StudentAnswerResponse> answers) {

		QuizAttemptResponseBuilder builder = QuizAttemptResponse.builder()
				.id(quizAttempt.getId())
				.questionPaperId(quizAttempt.getQuestionPaper().getId())
				.questionPaperTitle(quizAttempt.getQuestionPaper().getTitle())
				.startTime(quizAttempt.getStartTime())
				.endTime(quizAttempt.getEndTime())
				.completed(quizAttempt.isCompleted())
				.score(quizAttempt.getScore())
				.totalMarks(quizAttempt.getTotalMarks())
				.percentage(quizAttempt.getPercentage())
				.passed(quizAttempt.getPassed())
				.remainingTime(quizAttempt.getRemainingTime());

		if (includeAnswers && answers != null) {
			builder.answers(answers);
		}

		return builder.build();
	}
}