package trex.hackathon.smart_prep.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import trex.hackathon.smart_prep.model.QuestionPaper;
import trex.hackathon.smart_prep.model.QuizAttempt;
import trex.hackathon.smart_prep.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

	Page<QuizAttempt> findByStudent(User student, Pageable pageable);

	List<QuizAttempt> findByStudentAndCompletedTrue(User student);

	List<QuizAttempt> findByStudentAndCompletedFalse(User student);

	Page<QuizAttempt> findByQuestionPaper(QuestionPaper questionPaper, Pageable pageable);

	@Query("SELECT qa FROM QuizAttempt qa WHERE qa.student = ?1 AND qa.questionPaper = ?2 AND qa.completed = false")
	Optional<QuizAttempt> findIncompleteAttempt(User student, QuestionPaper questionPaper);

	@Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.student = ?1 AND qa.questionPaper = ?2")
	Integer countAttemptsByStudentAndQuestionPaper(User student, QuestionPaper questionPaper);

	@Query("SELECT AVG(qa.score) FROM QuizAttempt qa WHERE qa.questionPaper = ?1 AND qa.completed = true")
	Double getAverageScore(QuestionPaper questionPaper);
}