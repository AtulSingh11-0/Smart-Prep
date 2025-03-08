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
@Table ( name = "quiz_attempts" )
public class QuizAttempt {

	@Id
	@GeneratedValue ( strategy = GenerationType.IDENTITY )
	private Long id;

	@ManyToOne ( fetch = FetchType.LAZY )
	@JoinColumn ( name = "question_paper_id", nullable = false )
	private QuestionPaper questionPaper;

	@ManyToOne ( fetch = FetchType.LAZY )
	@JoinColumn ( name = "student_id", nullable = false )
	private User student;

	@Column ( name = "start_time", nullable = false )
	private LocalDateTime startTime;

	@Column ( name = "end_time" )
	private LocalDateTime endTime;

	@Column ( name = "is_completed" )
	private boolean completed;

	@Column ( name = "score" )
	private Integer score;

	@Column ( name = "total_marks" )
	private Integer totalMarks;

	@Column ( name = "percentage" )
	private Double percentage;

	@Column ( name = "passed" )
	private Boolean passed;

	@OneToMany ( mappedBy = "quizAttempt", cascade = CascadeType.ALL, orphanRemoval = true )
	private List< StudentAnswer > responses = new ArrayList<>();

	@Column ( name = "created_at" )
	private LocalDateTime createdAt;

	@Column ( name = "updated_at" )
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate () {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		if ( this.startTime == null ) {
			this.startTime = LocalDateTime.now();
		}
	}

	@PreUpdate
	protected void onUpdate () {
		this.updatedAt = LocalDateTime.now();
	}

	// Calculate remaining time in seconds
	@Transient
	public Long getRemainingTime () {
		if ( completed || endTime != null ) {
			return 0L;
		}

		if ( questionPaper.getDuration() == null ) {
			return Long.MAX_VALUE; // No time limit
		}

		LocalDateTime deadline = startTime.plusMinutes(questionPaper.getDuration());
		long remainingSeconds = java.time.Duration.between(LocalDateTime.now(), deadline).getSeconds();
		return Math.max(0, remainingSeconds);
	}

	// Check if the attempt has timed out
	@Transient
	public boolean isTimedOut () {
		if ( completed || endTime != null ) {
			return false;
		}

		if ( questionPaper.getDuration() == null ) {
			return false; // No time limit
		}

		LocalDateTime deadline = startTime.plusMinutes(questionPaper.getDuration());
		return LocalDateTime.now().isAfter(deadline);
	}
}