package trex.hackathon.smart_prep.dto.response;

import lombok.*;
import trex.hackathon.smart_prep.model.Question;
import trex.hackathon.smart_prep.model.StudentAnswer;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentAnswerResponse {
	private Long id;
	private Long questionId;
	private String questionText;
	private Question.QuestionType questionType;
	private Question.DifficultyLevel difficultyLevel;
	private Integer marks;
	private List<String> options;
	private Integer selectedOptionIndex;
	private Integer correctOption;
	private String textAnswer;
	private String correctAnswer;
	private Boolean isCorrect;
	private Integer marksAwarded;
	private String aiFeedback;
	private String teacherFeedback;

	public static StudentAnswerResponse fromStudentAnswer(StudentAnswer answer, boolean includeCorrectAnswers) {

		StudentAnswerResponseBuilder builder = StudentAnswerResponse.builder()
				.id(answer.getId())
				.questionId(answer.getQuestion().getId())
				.questionText(answer.getQuestion().getQuestionText())
				.questionType(answer.getQuestion().getQuestionType())
				.difficultyLevel(answer.getQuestion().getDifficultyLevel())
				.marks(answer.getQuestion().getMarks())
				.options(answer.getQuestion().getOptions())
				.selectedOptionIndex(answer.getSelectedOptionIndex())
				.textAnswer(answer.getTextAnswer())
				.correctOption(answer.getQuestion().getCorrectOptionIndex())
				.correctAnswer(answer.getQuestion().getModelAnswer());

		// Only include grading information if requested or attempt is completed
		if (includeCorrectAnswers) {
			builder.isCorrect(answer.getIsCorrect())
					.marksAwarded(answer.getMarksAwarded())
					.aiFeedback(answer.getAiFeedback())
					.teacherFeedback(answer.getTeacherFeedback());
		}

		return builder.build();
	}
}