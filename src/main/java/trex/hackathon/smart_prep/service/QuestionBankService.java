package trex.hackathon.smart_prep.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trex.hackathon.smart_prep.dto.request.QuestionBankRequest;
import trex.hackathon.smart_prep.dto.response.QuestionBankResponse;
import trex.hackathon.smart_prep.exception.QuestionBankAlreadyExistsException;
import trex.hackathon.smart_prep.exception.QuestionBankNotFoundException;
import trex.hackathon.smart_prep.exception.UnauthorizedAccessException;
import trex.hackathon.smart_prep.model.QuestionBank;
import trex.hackathon.smart_prep.model.User;
import trex.hackathon.smart_prep.repository.QuestionBankRepository;
import trex.hackathon.smart_prep.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class QuestionBankService {

	private final QuestionBankRepository questionBankRepository;
	private final UserRepository userRepository;

	public QuestionBankResponse createQuestionBank( QuestionBankRequest request, User creator ) {
		// Check if a question bank with this name already exists for this creator
		if ( questionBankRepository.existsByNameAndCreator(request.getName(), creator) ) {
			throw new QuestionBankAlreadyExistsException("You already have a question bank with this name");
		}

		// Create and save the question bank
		log.info(String.valueOf(request));
		QuestionBank questionBank = QuestionBank.builder()
				.name(request.getName())
				.subject(request.getSubject())
				.description(request.getDescription())
				.creator(creator)
				.isPublic(request.isPublic())
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		QuestionBank savedQuestionBank = questionBankRepository.save(questionBank);

		// return the response
		return QuestionBankResponse.fromQuestionBank(savedQuestionBank);
	}

	public Page<QuestionBankResponse> getQuestionBanksByCreator(User creator, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
		Page<QuestionBank> questionBanks = questionBankRepository.findByCreatorId(creator.getId(), pageable);

		return questionBanks.map(QuestionBankResponse::fromQuestionBank);
	}

	public List<QuestionBankResponse> getAllQuestionBanksByCreator(User creator) {
		List< QuestionBank > questionBanks = questionBankRepository.findAllByCreator(creator);

		return questionBanks.stream()
				.map(QuestionBankResponse::fromQuestionBank).toList();
	}

	public QuestionBankResponse getQuestionBankById(Long id, User currentUser) {
		QuestionBank questionBank = questionBankRepository.findById(id)
				.orElseThrow(() -> new QuestionBankNotFoundException("Question bank not found"));

		// Check if user has access (is creator or bank is public)
		if (!questionBank.isPublic() && !questionBank.getCreator().getId().equals(currentUser.getId())) {
			throw new UnauthorizedAccessException("You don't have access to this question bank");
		}

		return QuestionBankResponse.fromQuestionBank(questionBank);
	}
	
	public QuestionBankResponse updateQuestionBank(Long id, QuestionBankRequest request, User currentUser) {
		QuestionBank questionBank = questionBankRepository.findById(id)
				.orElseThrow(() -> new QuestionBankNotFoundException("Question bank not found"));

		// check if user has access (is creator)
		if ( !questionBank.getCreator().getId().equals(currentUser.getId()) ) {
			throw new UnauthorizedAccessException("You can only update your own question banks");
		}

		// if name is changed, check if a question bank with this name already exists for this creator
		if ( !questionBank.getName().equals(request.getName()) &&
				questionBankRepository.existsByNameAndCreator(request.getName(), currentUser) ) {
			throw new QuestionBankAlreadyExistsException("You already have a question bank with this name");
		}

		// update the question bank
		questionBank.setName(request.getName());
		questionBank.setDescription(request.getDescription());
		questionBank.setPublic(request.isPublic());
		questionBank.setUpdatedAt(LocalDateTime.now());

		QuestionBank updatedQuestionBank = questionBankRepository.save(questionBank);

		return QuestionBankResponse.fromQuestionBank(updatedQuestionBank);
	}

	public void deleteQuestionBank(Long id, User currentUser) {
		QuestionBank questionBank = questionBankRepository.findById(id)
				.orElseThrow(() -> new QuestionBankNotFoundException("Question bank not found"));

		// check if user has access (is creator)
		if ( !questionBank.getCreator().getId().equals(currentUser.getId()) ) {
			throw new UnauthorizedAccessException("You can only delete your own question banks");
		}

		// delete the question bank
		questionBankRepository.delete(questionBank);
	}
}
