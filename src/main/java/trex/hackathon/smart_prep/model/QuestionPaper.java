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
@Table (name = "question_papers")
public class QuestionPaper {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_bank_id", nullable = false)
	private QuestionBank questionBank;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator_id", nullable = false)
	private User creator;

	@Column(name = "duration_in_minutes")
	private Integer duration;

	@Column(name = "total_questions")
	private Integer totalQuestions;

	@Enumerated(EnumType.STRING)
	@Column(name = "difficulty_level")
	private DifficultyLevel difficultyLevel;

	@Column(name = "total_marks")
	private Integer totalMarks;

	@Column(name = "passing_marks")
	private Integer passingMarks;

	@OneToMany(mappedBy = "questionPaper", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Question> questions = new ArrayList<>();

	@Column(name = "is_published")
	private boolean published;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	protected void prePersist() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	public enum DifficultyLevel {
		EASY, MEDIUM, HARD
	}
}
