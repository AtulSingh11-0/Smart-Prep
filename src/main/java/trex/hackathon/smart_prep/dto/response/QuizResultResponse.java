package trex.hackathon.smart_prep.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trex.hackathon.smart_prep.model.QuizAttempt;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizResultResponse {
	private Long id;
	private Long questionPaperId;
	private String questionPaperTitle;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private Integer timeTakenMinutes;
	private Integer score;
	private Integer totalMarks;
	private Double percentage;
	private Boolean passed;
	private List<StudentAnswerResponse> answers;

	public static QuizResultResponse fromQuizAttempt(
			QuizAttempt quizAttempt, List<StudentAnswerResponse> answers) {

		// Calculate time taken in minutes
		long timeTakenMinutes = 0;
		if (quizAttempt.getStartTime() != null && quizAttempt.getEndTime() != null) {
			timeTakenMinutes = ChronoUnit.MINUTES.between(
					quizAttempt.getStartTime(), quizAttempt.getEndTime());
		}

		return QuizResultResponse.builder()
				.id(quizAttempt.getId())
				.questionPaperId(quizAttempt.getQuestionPaper().getId())
				.questionPaperTitle(quizAttempt.getQuestionPaper().getTitle())
				.startTime(quizAttempt.getStartTime())
				.endTime(quizAttempt.getEndTime())
				.timeTakenMinutes((int) timeTakenMinutes)
				.score(quizAttempt.getScore())
				.totalMarks(quizAttempt.getTotalMarks())
				.percentage(quizAttempt.getPercentage())
				.passed(quizAttempt.getPassed())
				.answers(answers)
				.build();
	}
}