package trex.hackathon.smart_prep.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trex.hackathon.smart_prep.dto.request.QuestionRequest;
import trex.hackathon.smart_prep.dto.response.QuestionResponse;
import trex.hackathon.smart_prep.exception.QuestionPaperNotFoundException;
import trex.hackathon.smart_prep.exception.QuestionValidationException;
import trex.hackathon.smart_prep.exception.UnauthorizedAccessException;
import trex.hackathon.smart_prep.model.Question;
import trex.hackathon.smart_prep.model.QuestionPaper;
import trex.hackathon.smart_prep.model.User;
import trex.hackathon.smart_prep.repository.QuestionPaperRepository;
import trex.hackathon.smart_prep.repository.QuestionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionService {

	private final QuestionRepository questionRepository;
	private final QuestionPaperRepository questionPaperRepository;

	public QuestionResponse createQuestion( Long questionPaperId, QuestionRequest request, User currentUser ) {
		// find the question paper
		QuestionPaper questionPaper = questionPaperRepository.findById(questionPaperId)
				.orElseThrow( () -> new QuestionPaperNotFoundException("Question Paper not found") );

		// check if user is the creator of the question paper
		if ( !questionPaper.getCreator().getId().equals(currentUser.getId()) ) {
			throw new UnauthorizedAccessException("You can only add questions to your own question papers");
		}

		// validate question based on type
		validateQuestion(request);

		// check if adding this question would exceed the totalQuestions limit
		Integer currentTotalQuestions = questionRepository.getTotalQuestionsByQuestionPaperId(questionPaperId);
		if ( currentTotalQuestions != null &&
		questionPaper.getTotalQuestions() != null &&
		currentTotalQuestions >= questionPaper.getTotalQuestions() ) {
			throw new QuestionValidationException("Cannot add more questions. The limit of " +
					questionPaper.getTotalQuestions() + " questions has been reached");
		}

		// check if adding this question would exceed the totalMarks limit
		Integer currentTotalMarks = questionRepository.getTotalMarksByQuestionPaperId(questionPaperId);
		if ( currentTotalMarks != null &&
				questionPaper.getTotalMarks() != null &&
				currentTotalMarks + request.getMarks() > questionPaper.getTotalMarks() ) {
			throw new QuestionValidationException("Cannot add this question. The total marks would exceed the limit of " +
					questionPaper.getTotalMarks());
		}

		// create and save the question
		Question question = Question.builder()
				.questionText(request.getQuestionText())
				.questionType(request.getQuestionType())
				.difficultyLevel(request.getDifficultyLevel())
				.marks(request.getMarks())
				.expectedTimeSeconds(request.getExpectedTimeSeconds())
				.options(request.getOptions())
				.correctOptionIndex(request.getCorrectOptionIndex())
				.modelAnswer(request.getModelAnswer())
				.questionPaper(questionPaper)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		Question savedQuestion = questionRepository.save(question);

		return QuestionResponse.fromQuestion(savedQuestion);
	}

	public Page<QuestionResponse> getQuestionsByQuestionPaper(Long questionPaperId, User currentUser, int page,
	                                                          int size) {
		// find the question paper
		QuestionPaper questionPaper = questionPaperRepository.findById(questionPaperId)
				.orElseThrow( () -> new QuestionPaperNotFoundException("Question Paper not found") );

		// check if user has access (is the creator or question paper is published)
		if ( !questionPaper.isPublished() &&  !questionPaper.getCreator().getId().equals(currentUser.getId()) ) {
			throw new UnauthorizedAccessException("You dont have access to questions in this question paper");
		}

		// get questions
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
		Page< Question > questions = questionRepository.findByQuestionPaperId(questionPaperId, pageable);

		return questions.map(QuestionResponse::fromQuestion);
	}

	public List<QuestionResponse> getAllQuestionsByQuestionPaper(Long questionPaperId, User currentUser) {
		// find the question paper
		QuestionPaper questionPaper = questionPaperRepository.findById(questionPaperId)
				.orElseThrow( () -> new QuestionPaperNotFoundException("Question Paper not found") );

		// check if user has access (is the creator or question paper is published)
		if ( !questionPaper.isPublished() &&  !questionPaper.getCreator().getId().equals(currentUser.getId()) ) {
			throw new UnauthorizedAccessException("You dont have access to questions in this question paper");
		}

		// get all questions
		List< Question > questions = questionRepository.findByQuestionPaperId(questionPaperId);

		return questions.stream()
				.map(QuestionResponse::fromQuestion)
				.toList();
	}

	public QuestionResponse getQuestionById(Long id, User currentUser) {
		Question question = questionRepository.findById(id)
				.orElseThrow(() -> new QuestionPaperNotFoundException("Question not found"));

		QuestionPaper questionPaper = question.getQuestionPaper();

		// check if user has access (is the creator or question paper is published)
		if ( !questionPaper.isPublished() && !questionPaper.getCreator().getId().equals(currentUser.getId()) ) {
			throw new UnauthorizedAccessException("You dont have access to this question");
		}

		return QuestionResponse.fromQuestion(question);
	}

	public QuestionResponse updateQuestion(Long id, QuestionRequest request, User currentUser) {
		// find the question
		Question question = questionRepository.findById(id)
				.orElseThrow(() -> new QuestionPaperNotFoundException("Question not found"));

		QuestionPaper questionPaper = question.getQuestionPaper();

		// check if user is the creator of the question paper
		if ( !questionPaper.getCreator().getId().equals(currentUser.getId()) ) {
			throw new UnauthorizedAccessException("You can only update questions in your own question papers");
		}

		// validate question based on type
		validateQuestion(request);

		// calculate the difference in marks if the marks are updated
		int marksDifference = request.getMarks() - question.getMarks();

		if ( marksDifference > 0 ) {
			// check if adding this question would exceed the totalMarks limit
			Integer currentTotalMarks = questionRepository.getTotalMarksByQuestionPaperId(questionPaper.getId());
			if ( currentTotalMarks != null &&
					questionPaper.getTotalMarks() != null &&
					( currentTotalMarks + marksDifference ) > questionPaper.getTotalMarks() ) {
				throw new QuestionValidationException("Cannot update this question. The total marks would exceed the limit of " +
						questionPaper.getTotalMarks());
			}
		}

		// update the question
		question.setQuestionText(request.getQuestionText());
		question.setQuestionType(request.getQuestionType());
		question.setDifficultyLevel(request.getDifficultyLevel());
		question.setMarks(request.getMarks());
		question.setExpectedTimeSeconds(request.getExpectedTimeSeconds());
		question.setOptions(request.getOptions());
		question.setCorrectOptionIndex(request.getCorrectOptionIndex());
		question.setModelAnswer(request.getModelAnswer());
		question.setUpdatedAt(LocalDateTime.now());

		Question updatedQuestion = questionRepository.save(question);

		return QuestionResponse.fromQuestion(updatedQuestion);
	}

	public void deleteQuestion(Long id, User currentUser) {
		// find the question
		Question question = questionRepository.findById(id)
				.orElseThrow(() -> new QuestionPaperNotFoundException("Question not found"));

		QuestionPaper questionPaper = question.getQuestionPaper();

		// check if user is the creator of the question paper
		if ( !questionPaper.getCreator().getId().equals(currentUser.getId()) ) {
			throw new UnauthorizedAccessException("You can only delete questions in your own question papers");
		}

		// delete the question
		questionRepository.delete(question);
	}
	
	private void validateQuestion( QuestionRequest request ) {
		if ( request.getQuestionType() == Question.QuestionType.MCQ ) {
			// for MCQs, validate options and correct option
			if ( request.getOptions() == null || request.getOptions().size() != 4 ) {
				throw new QuestionValidationException("MCQs must have exactly 4 options");
			}
			if (request.getCorrectOptionIndex() == null ||
					request.getCorrectOptionIndex() < 0 ||
					request.getCorrectOptionIndex() >= request.getOptions().size()) {
				throw new QuestionValidationException("Invalid correct option index");
			}
		} else if ( request.getQuestionType() == Question.QuestionType.SAQ ) {
			// for SAQs, validate model answer
			if ( request.getModelAnswer() == null || request.getModelAnswer().trim().isEmpty() ) {
				throw new QuestionValidationException("SAQs must have a model answer");
			}
		} else {
			throw new QuestionValidationException("Invalid question type");
		}
	}
}
