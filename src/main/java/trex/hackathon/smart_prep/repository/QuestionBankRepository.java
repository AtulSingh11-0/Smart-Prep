package trex.hackathon.smart_prep.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import trex.hackathon.smart_prep.model.QuestionBank;
import trex.hackathon.smart_prep.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long> {

	Page<QuestionBank> findByCreatorId(Long creatorId, Pageable pageable);

	List<QuestionBank> findAllByCreator ( User creator);

	Optional<QuestionBank> findByNameAndCreator(String name, User creator);

	boolean existsByNameAndCreator(String name, User creator);

	Page<QuestionBank> findByPublic ( boolean isPublic, Pageable pageable );
}