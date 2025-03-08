package trex.hackathon.smart_prep.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trex.hackathon.smart_prep.model.Question;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {

	private String questionText;
	private Question.QuestionType questionType;
	private Question.DifficultyLevel difficultyLevel;
	private Integer marks;
	private Integer expectedTimeSeconds;
	private List<String> options = new ArrayList<>();
	private Integer correctOptionIndex;
	private String modelAnswer;
}
