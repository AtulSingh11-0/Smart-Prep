package trex.hackathon.smart_prep.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import trex.hackathon.smart_prep.model.QuestionPaper;
import trex.hackathon.smart_prep.model.User;

import java.util.List;

@Repository
public interface QuestionPaperRepository extends JpaRepository< QuestionPaper, Long > {
	Page<QuestionPaper> findByQuestionBankId( Long questionBankId, Pageable pageable);

	List<QuestionPaper> findAllByQuestionBankId( Long questionBankId);

	Page<QuestionPaper> findByCreatorId(Long creatorId, Pageable pageable);

	List<QuestionPaper> findByCreator( User creator);

	Page<QuestionPaper> findByPublishedTrue(Pageable pageable);

	long countByQuestionBankId(Long questionBankId);
}
