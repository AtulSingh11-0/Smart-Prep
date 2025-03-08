package trex.hackathon.smart_prep.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "questions")
public class Question {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String questionText;

	@Enumerated(EnumType.STRING)
	@Column(name = "question_type", nullable = false)
	private QuestionType questionType;

	@Enumerated(EnumType.STRING)
	@Column(name = "difficulty_level", nullable = false)
	private DifficultyLevel difficultyLevel;

	@Column(name = "marks", nullable = false)
	private Integer marks;

	@Column(name = "expected_time_seconds")
	private Integer expectedTimeSeconds;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "question_options",
			joinColumns = @JoinColumn(name = "question_id"))
	@Column(name = "option_text", columnDefinition = "TEXT")
	private List<String> options = new ArrayList<>();

	@Column(name = "correct_option_index")
	private Integer correctOptionIndex;

	@Column(name = "model_answer", columnDefinition = "TEXT")
	private String modelAnswer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_paper_id", nullable = false)
	private QuestionPaper questionPaper;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public enum QuestionType {
		MCQ, SAQ
	}

	public enum DifficultyLevel {
		BEGINNER, INTERMEDIATE, ADVANCED
	}
}
