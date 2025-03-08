package trex.hackathon.smart_prep.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import trex.hackathon.smart_prep.model.Question;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository< Question, Long > {
	List<Question> findByQuestionPaperId( Long questionPaperId);

	Page<Question> findByQuestionPaperId( Long questionPaperId, Pageable pageable);

	void deleteByQuestionPaperId(Long questionPaperId);

	long countByQuestionPaperId(Long questionPaperId);

	@Query("SELECT SUM(q.marks) FROM Question q WHERE q.questionPaper.id = :questionPaperId")
	Integer getTotalMarksByQuestionPaperId(Long questionPaperId);

	@Query ("SELECT COUNT(q) FROM Question q WHERE q.questionPaper.id = :questionPaperId")
	Integer getTotalQuestionsByQuestionPaperId(Long questionPaperId);
}
