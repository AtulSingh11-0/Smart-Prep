package trex.hackathon.smart_prep.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trex.hackathon.smart_prep.dto.request.SubmitAnswerRequest;
import trex.hackathon.smart_prep.dto.response.*;
import trex.hackathon.smart_prep.dto.response.AvailableQuestionPaperResponse;
import trex.hackathon.smart_prep.exception.InvalidQuizActionException;
import trex.hackathon.smart_prep.exception.QuestionNotFoundException;
import trex.hackathon.smart_prep.exception.QuestionPaperNotFoundException;
import trex.hackathon.smart_prep.exception.QuizAttemptNotFoundException;
import trex.hackathon.smart_prep.model.*;
import trex.hackathon.smart_prep.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class QuizAttemptService {

	private final QuestionPaperRepository questionPaperRepository;
	private final QuestionRepository questionRepository;
	private final QuizAttemptRepository quizAttemptRepository;
	private final StudentAnswerRepository studentAnswerRepository;

	public Page< AvailableQuestionPaperResponse > getAvailableQuestionPapers( User student, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
		Page<QuestionPaper> questionPapers = questionPaperRepository.findByPublishedTrue(pageable);

		return questionPapers.map(questionPaper -> {
			Integer attempts = quizAttemptRepository.countAttemptsByStudentAndQuestionPaper(student, questionPaper);

			Optional<QuizAttempt> pendingAttempt = quizAttemptRepository.findIncompleteAttempt(student, questionPaper);
			boolean hasPending = pendingAttempt.isPresent();

			return AvailableQuestionPaperResponse.fromQuestionPaper(questionPaper, attempts, hasPending);
		});
	}

	public QuizAttemptResponse startQuizAttempt(Long questionPaperId, User student) {
		// Find the question paper
		QuestionPaper questionPaper = questionPaperRepository.findById(questionPaperId)
				.orElseThrow(() -> new QuestionPaperNotFoundException("Question paper not found"));

		// Check if the question paper is published
		if (!questionPaper.isPublished()) {
			throw new InvalidQuizActionException("This question paper is not available for attempts");
		}

		// Check if there's an incomplete attempt
		Optional<QuizAttempt> existingAttempt = quizAttemptRepository.findIncompleteAttempt(student, questionPaper);

		if (existingAttempt.isPresent()) {
			// Return the existing attempt
			QuizAttempt attempt = existingAttempt.get();

			// Check if the attempt has timed out
			if (attempt.isTimedOut()) {
				// Auto-submit the attempt
				return submitQuizAttempt(attempt.getId(), student);
			}

			// Get the answers for this attempt
			List<StudentAnswer> answers = studentAnswerRepository.findByQuizAttempt(attempt);

			List<StudentAnswerResponse> answerResponses = answers.stream()
					.map(answer -> StudentAnswerResponse.fromStudentAnswer(answer, true))
					.toList();

			return QuizAttemptResponse.fromQuizAttempt(attempt, true, answerResponses);
		}

		// Create a new attempt
		QuizAttempt quizAttempt = QuizAttempt.builder()
				.questionPaper(questionPaper)
				.student(student)
				.startTime(LocalDateTime.now())
				.completed(false)
				.build();

		QuizAttempt savedAttempt = quizAttemptRepository.save(quizAttempt);

		// Return the new attempt with no answers yet
		return QuizAttemptResponse.fromQuizAttempt(savedAttempt, true, new ArrayList<>());
	}

	public StudentAnswerResponse submitAnswer(Long attemptId, SubmitAnswerRequest request, User student) {
		// Find the attempt
		QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
				.orElseThrow(() -> new QuizAttemptNotFoundException("Quiz attempt not found"));

		// Verify this attempt belongs to the current student
		if (!attempt.getStudent().getId().equals(student.getId())) {
			throw new InvalidQuizActionException("You can only submit answers to your own quiz attempts");
		}

		// Check if the attempt is not completed
		if (attempt.isCompleted()) {
			throw new InvalidQuizActionException("Cannot submit answers to a completed quiz attempt");
		}

		// Check if the attempt has timed out
		if (attempt.isTimedOut()) {
			// Auto-submit the attempt and throw an exception
			submitQuizAttempt(attemptId, student);
			throw new InvalidQuizActionException("Quiz attempt has timed out and was automatically submitted");
		}

		// Find the question
		Question question = questionRepository.findById(request.getQuestionId())
				.orElseThrow(() -> new QuestionNotFoundException("Question not found"));

		// Check if the question belongs to the question paper
		if (!question.getQuestionPaper().getId().equals(attempt.getQuestionPaper().getId())) {
			throw new InvalidQuizActionException("Question does not belong to this quiz attempt");
		}

		// Check if an answer already exists for this question, if so, update it
		Optional<StudentAnswer> existingAnswer = studentAnswerRepository.findByQuizAttemptAndQuestion(attempt, question);

		StudentAnswer answer;

		if (existingAnswer.isPresent()) {
			answer = existingAnswer.get();
			if (question.getQuestionType() == Question.QuestionType.MCQ) {
				answer.setSelectedOptionIndex(request.getSelectedOptionIndex());
			} else {
				answer.setTextAnswer(request.getTextAnswer());
			}
			answer.setUpdatedAt(LocalDateTime.now());
		} else {
			// Create a new answer
			answer = StudentAnswer.builder()
					.quizAttempt(attempt)
					.question(question)
					.selectedOptionIndex(question.getQuestionType() == Question.QuestionType.MCQ
							? request.getSelectedOptionIndex() : null)
					.textAnswer(question.getQuestionType() == Question.QuestionType.SAQ
							? request.getTextAnswer() : null)
					.build();
		}

		StudentAnswer savedAnswer = studentAnswerRepository.save(answer);

		// Return the answer
		return StudentAnswerResponse.fromStudentAnswer(savedAnswer, true);
	}

	public QuizAttemptResponse getQuizAttempt(Long attemptId, User student) {
		// Find the attempt
		QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
				.orElseThrow(() -> new QuizAttemptNotFoundException("Quiz attempt not found"));

		// Verify this attempt belongs to the current student
		if (!attempt.getStudent().getId().equals(student.getId())) {
			throw new InvalidQuizActionException("You can only view your own quiz attempts");
		}

		// Check if the attempt has timed out
		if (!attempt.isCompleted() && attempt.isTimedOut()) {
			// Auto-submit the attempt
			return submitQuizAttempt(attemptId, student);
		}

		// Get the answers for this attempt
		List<StudentAnswer> answers = studentAnswerRepository.findByQuizAttempt(attempt);

		List<StudentAnswerResponse> answerResponses = answers.stream()
				.map(answer -> StudentAnswerResponse.fromStudentAnswer(answer, attempt.isCompleted()))
				.toList();

		return QuizAttemptResponse.fromQuizAttempt(attempt, true, answerResponses);
	}

	public QuizAttemptResponse submitQuizAttempt(Long attemptId, User student) {
		// Find the attempt
		QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
				.orElseThrow(() -> new QuizAttemptNotFoundException("Quiz attempt not found"));

		// Verify this attempt belongs to the current student
		if (!attempt.getStudent().getId().equals(student.getId())) {
			throw new InvalidQuizActionException("You can only submit your own quiz attempts");
		}

		// Check if the attempt is not already completed
		if (attempt.isCompleted()) {
			throw new InvalidQuizActionException("Quiz attempt is already completed");
		}

		// Mark the attempt as completed
		attempt.setCompleted(true);
		attempt.setEndTime(LocalDateTime.now());

		// Get all questions from the question paper
		List<Question> questions = questionRepository.findByQuestionPaperId(attempt.getQuestionPaper().getId());

		// Calculate the total possible marks
		int totalMarks = questions.stream().mapToInt(Question::getMarks).sum();
		attempt.setTotalMarks(totalMarks);

		// Get the student's answers
		List<StudentAnswer> answers = studentAnswerRepository.findByQuizAttempt(attempt);

		// Grade the answers
		int totalScore = 0;

		for (StudentAnswer answer : answers) {
			Question question = answer.getQuestion();

			// Grade based on question type
			if (question.getQuestionType() == Question.QuestionType.MCQ) {
				// For MCQs, check if the selected option is correct
				if (answer.getSelectedOptionIndex() != null &&
						answer.getSelectedOptionIndex().equals(question.getCorrectOptionIndex())) {
					answer.setIsCorrect(true);
					answer.setMarksAwarded(question.getMarks());
					totalScore += question.getMarks();
				} else {
					answer.setIsCorrect(false);
					answer.setMarksAwarded(0);
				}
			} else {
				// For SAQs, we can't auto-grade, but we can mark as pending for AI/teacher review
				// For now just mark as incorrect until AI/teacher review
				answer.setIsCorrect(false);
				answer.setMarksAwarded(0);
			}

			studentAnswerRepository.save(answer);
		}

		// Update the attempt with the score
		attempt.setScore(totalScore);

		// Calculate percentage
		double percentage = totalMarks > 0 ? (double) totalScore / totalMarks * 100 : 0;
		attempt.setPercentage(percentage);

		// Check if passed
		Integer passingMarks = attempt.getQuestionPaper().getPassingMarks();
		if (passingMarks != null) {
			attempt.setPassed(totalScore >= passingMarks);
		}

		// Save the attempt
		QuizAttempt updatedAttempt = quizAttemptRepository.save(attempt);

		// Convert answers to response objects
		List<StudentAnswerResponse> answerResponses = answers.stream()
				.map(answer -> StudentAnswerResponse.fromStudentAnswer(answer, true))
				.toList();

		return QuizAttemptResponse.fromQuizAttempt(updatedAttempt, true, answerResponses);
	}

	public QuizResultResponse getQuizResult(Long attemptId, User student) {
		// Find the attempt
		QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
				.orElseThrow(() -> new QuizAttemptNotFoundException("Quiz attempt not found"));

		// Verify this attempt belongs to the current student
		if (!attempt.getStudent().getId().equals(student.getId())) {
			throw new InvalidQuizActionException("You can only view results of your own quiz attempts");
		}

		// Check if the attempt is completed
		if (!attempt.isCompleted()) {
			throw new InvalidQuizActionException("Quiz attempt is not yet completed");
		}

		// Get the answers for this attempt
		List<StudentAnswer> answers = studentAnswerRepository.findByQuizAttempt(attempt);

		List<StudentAnswerResponse> answerResponses = answers.stream()
				.map(answer -> StudentAnswerResponse.fromStudentAnswer(answer, true))
				.toList();

		return QuizResultResponse.fromQuizAttempt(attempt, answerResponses);
	}

	public Page<QuizAttemptResponse> getStudentAttempts(User student, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<QuizAttempt> attempts = quizAttemptRepository.findByStudent(student, pageable);

		return attempts.map(attempt -> {
			List<StudentAnswer> answers = studentAnswerRepository.findByQuizAttempt(attempt);
			List<StudentAnswerResponse> answerResponses = answers.stream()
					.map(answer -> StudentAnswerResponse.fromStudentAnswer(answer, attempt.isCompleted()))
					.toList();

			return QuizAttemptResponse.fromQuizAttempt(attempt, true, answerResponses);
		});
	}

	public List<QuizAttemptResponse> getCompletedAttempts(User student) {
		List<QuizAttempt> completedAttempts = quizAttemptRepository.findByStudentAndCompletedTrue(student);

		return completedAttempts.stream().map(attempt -> {
			List<StudentAnswer> answers = studentAnswerRepository.findByQuizAttempt(attempt);
			List<StudentAnswerResponse> answerResponses = answers.stream()
					.map(answer -> StudentAnswerResponse.fromStudentAnswer(answer, true))
					.toList();

			return QuizAttemptResponse.fromQuizAttempt(attempt, true, answerResponses);
		}).toList();
	}

	public List<QuizAttemptResponse> getIncompleteAttempts(User student) {
		List<QuizAttempt> incompleteAttempts = quizAttemptRepository.findByStudentAndCompletedFalse(student);

		return incompleteAttempts.stream().map(attempt -> {
			List<StudentAnswer> answers = studentAnswerRepository.findByQuizAttempt(attempt);
			List<StudentAnswerResponse> answerResponses = answers.stream()
					.map(answer -> StudentAnswerResponse.fromStudentAnswer(answer, true))
					.toList();

			return QuizAttemptResponse.fromQuizAttempt(attempt, true, answerResponses);
		}).toList();
	}
}