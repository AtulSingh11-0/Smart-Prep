package trex.hackathon.smart_prep.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_answers")
public class StudentAnswer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quiz_attempt_id", nullable = false)
	private QuizAttempt quizAttempt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_id", nullable = false)
	private Question question;

	@Column(name = "selected_option_index")
	private Integer selectedOptionIndex;

	@Column(name = "text_answer", columnDefinition = "TEXT")
	private String textAnswer;

	@Column(name = "is_correct")
	private Boolean isCorrect;

	@Column(name = "marks_awarded")
	private Integer marksAwarded;

	@Column(name = "ai_feedback", columnDefinition = "TEXT")
	private String aiFeedback;

	@Column(name = "teacher_feedback", columnDefinition = "TEXT")
	private String teacherFeedback;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}