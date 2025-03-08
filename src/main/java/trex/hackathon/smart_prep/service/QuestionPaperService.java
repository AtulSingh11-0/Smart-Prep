package trex.hackathon.smart_prep.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trex.hackathon.smart_prep.dto.request.QuestionPaperRequest;
import trex.hackathon.smart_prep.dto.response.QuestionPaperResponse;
import trex.hackathon.smart_prep.exception.QuestionBankNotFoundException;
import trex.hackathon.smart_prep.exception.QuestionPaperNotFoundException;
import trex.hackathon.smart_prep.exception.UnauthorizedAccessException;
import trex.hackathon.smart_prep.model.QuestionBank;
import trex.hackathon.smart_prep.model.QuestionPaper;
import trex.hackathon.smart_prep.model.User;
import trex.hackathon.smart_prep.repository.QuestionBankRepository;
import trex.hackathon.smart_prep.repository.QuestionPaperRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionPaperService {

	private final QuestionPaperRepository questionPaperRepository;
	private final QuestionBankRepository questionBankRepository;

	public QuestionPaperResponse createQuestionPaper( QuestionPaperRequest request, User currentUser ) {
		// find the question bank
		QuestionBank questionBank = questionBankRepository.findById(request.getQuestionBankId())
				.orElseThrow(() -> new QuestionBankNotFoundException("Question bank not found"));

		// check if the current user owns the question bank
		if ( !questionBank.getCreator().getId().equals(currentUser.getId()) ) {
			throw new UnauthorizedAccessException("You can only create question paper in your own question banks");
		}

		// create the question paper
		QuestionPaper questionPaper = QuestionPaper.builder()
				.title(request.getTitle())
				.description(request.getDescription())
				.questionBank(questionBank)
				.creator(currentUser)
				.duration(request.getDuration())
				.totalQuestions(request.getTotalQuestions())
				.difficultyLevel(request.getDifficultyLevel())
				.totalMarks(request.getTotalMarks())
				.passingMarks(request.getPassingMarks())
				.published(request.isPublished())
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		QuestionPaper savedQuestionPaper = questionPaperRepository.save(questionPaper);

		return QuestionPaperResponse.fromQuestionPaper(savedQuestionPaper);
	}

	public Page<QuestionPaperResponse> getQuestionPapersByQuestionBank(Long questionBankId, User currentUser, int page,
	                                                                   int size) {
		// find the question bank
		QuestionBank questionBank = questionBankRepository.findById(questionBankId)
				.orElseThrow(() -> new QuestionBankNotFoundException("Question bank not found"));

		// check if the current user owns the question bank
		if ( !questionBank.getCreator().getId().equals(currentUser.getId()) ) {
			throw new UnauthorizedAccessException("You can only view question papers in your own question banks");
		}

		// get the question papers
		Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
		Page< QuestionPaper > questionPapers = questionPaperRepository.findByQuestionBankId(questionBankId, pageable);

		return questionPapers.map(QuestionPaperResponse::fromQuestionPaper);
	}

	public List<QuestionPaperResponse> getAllQuestionPapersByQuestionBank(Long questionBankId, User currentUser) {
		// find the question bank
		QuestionBank questionBank = questionBankRepository.findById(questionBankId)
				.orElseThrow(() -> new QuestionBankNotFoundException("Question bank not found"));

		// check if the current user has access to question bank
		if ( !questionBank.getCreator().getId().equals(currentUser.getId()) ) {
			throw new UnauthorizedAccessException("You can only view question papers in your own question banks");
		}

		// get all question papers
		List< QuestionPaper > questionPapers = questionPaperRepository.findAllByQuestionBankId(questionBankId);

		return questionPapers.stream()
				.map(QuestionPaperResponse::fromQuestionPaper)
				.toList();
	}

	public QuestionPaperResponse getQuestionPaperById(Long id, User currentUser) {
		// find the question paper
		QuestionPaper questionPaper = questionPaperRepository.findById(id)
				.orElseThrow(() -> new QuestionPaperNotFoundException("Question paper not found"));

		QuestionBank questionBank = questionPaper.getQuestionBank();

		// check if user has access to this bank
		if ( !questionBank.isPublic() && !questionBank.getCreator().getId().equals(currentUser.getId()) ) {
			throw new UnauthorizedAccessException("You don't have access to this question paper");
		}

		return QuestionPaperResponse.fromQuestionPaper(questionPaper);
	}

	public QuestionPaperResponse updateQuestionPaper(Long id, QuestionPaperRequest request, User currentUser) {
		// find the question paper
		QuestionPaper questionPaper = questionPaperRepository.findById(id)
				.orElseThrow(() -> new QuestionPaperNotFoundException("Question paper not found"));

		// check if user is the creator
		if ( !questionPaper.getCreator().getId().equals(currentUser.getId()) ) {
			throw new UnauthorizedAccessException("You can only update your own question papers");
		}

		// update the question paper details
		questionPaper.setTitle(request.getTitle());
		questionPaper.setDescription(request.getDescription());
		questionPaper.setDuration(request.getDuration());
		questionPaper.setTotalQuestions(request.getTotalQuestions());
		questionPaper.setDifficultyLevel(request.getDifficultyLevel());
		questionPaper.setTotalMarks(request.getTotalMarks());
		questionPaper.setPassingMarks(request.getPassingMarks());
		questionPaper.setPublished(request.isPublished());
		questionPaper.setUpdatedAt(LocalDateTime.now());

		// save and return the updated question paper
		QuestionPaper savedQuestionPaper = questionPaperRepository.save(questionPaper);
		return QuestionPaperResponse.fromQuestionPaper(savedQuestionPaper);
	}

	public void deleteQuestionPaper(Long id, User currentUser) {
		// find the question paper
		QuestionPaper questionPaper = questionPaperRepository.findById(id)
				.orElseThrow(() -> new QuestionPaperNotFoundException("Question paper not found"));

		// check if user is the creator
		if ( !questionPaper.getCreator().getId().equals(currentUser.getId()) ) {
			throw new UnauthorizedAccessException("You can only delete your own question papers");
		}

		// delete the question paper
		questionPaperRepository.delete(questionPaper);
	}

	public QuestionPaperResponse togglePublishStatus(Long id, User currentUser) {
		// find the question paper
		QuestionPaper questionPaper = questionPaperRepository.findById(id)
				.orElseThrow(() -> new QuestionPaperNotFoundException("Question paper not found"));

		// check if user is the creator
		if ( !questionPaper.getCreator().getId().equals(currentUser.getId()) ) {
			throw new UnauthorizedAccessException("You can only publish/unpublish your own question papers");
		}

		// toggle publish status
		questionPaper.setPublished(!questionPaper.isPublished());
		questionPaper.setUpdatedAt(LocalDateTime.now());

		QuestionPaper updatedQuestionPaper = questionPaperRepository.save(questionPaper);

		return QuestionPaperResponse.fromQuestionPaper(updatedQuestionPaper);
	}
}
