package trex.hackathon.smart_prep.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trex.hackathon.smart_prep.model.QuestionPaper;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionPaperRequest {

	private String title;
	private String description;
	private Long questionBankId;
	private Integer duration;
	private Integer totalQuestions;
	private QuestionPaper.DifficultyLevel difficultyLevel;
	private Integer totalMarks;
	private Integer passingMarks;
	private boolean published;
}
