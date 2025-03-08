package trex.hackathon.smart_prep.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import trex.hackathon.smart_prep.model.Question;
import trex.hackathon.smart_prep.model.QuizAttempt;
import trex.hackathon.smart_prep.model.StudentAnswer;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {

	List<StudentAnswer> findByQuizAttempt(QuizAttempt quizAttempt);

	Optional<StudentAnswer> findByQuizAttemptAndQuestion(QuizAttempt quizAttempt, Question question);

	void deleteByQuizAttempt(QuizAttempt quizAttempt);

	long countByQuizAttempt(QuizAttempt quizAttempt);

	long countByQuizAttemptAndIsCorrectTrue(QuizAttempt quizAttempt);
}