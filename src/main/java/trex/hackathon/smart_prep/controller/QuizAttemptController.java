package trex.hackathon.smart_prep.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import trex.hackathon.smart_prep.dto.request.SubmitAnswerRequest;
import trex.hackathon.smart_prep.dto.response.*;
import trex.hackathon.smart_prep.dto.response.AvailableQuestionPaperResponse;
import trex.hackathon.smart_prep.model.User;
import trex.hackathon.smart_prep.service.QuizAttemptService;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizAttemptController {

	private final QuizAttemptService quizAttemptService;

	@GetMapping("/available")
	@PreAuthorize("hasRole('STUDENT')")
	public ResponseEntity<Page< AvailableQuestionPaperResponse >> getAvailableQuizzes(
			@AuthenticationPrincipal User currentUser,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		return ResponseEntity.ok(quizAttemptService.getAvailableQuestionPapers(currentUser, page, size));
	}

	@PostMapping("/start/{questionPaperId}")
	@PreAuthorize("hasRole('STUDENT')")
	public ResponseEntity<QuizAttemptResponse> startQuiz(
			@PathVariable Long questionPaperId,
			@AuthenticationPrincipal User currentUser) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(quizAttemptService.startQuizAttempt(questionPaperId, currentUser));
	}

	@GetMapping("/attempt/{attemptId}")
	@PreAuthorize("hasRole('STUDENT')")
	public ResponseEntity<QuizAttemptResponse> getQuizAttempt(
			@PathVariable Long attemptId,
			@AuthenticationPrincipal User currentUser) {

		return ResponseEntity.ok(quizAttemptService.getQuizAttempt(attemptId, currentUser));
	}

	@PostMapping("/attempt/{attemptId}/answer")
	@PreAuthorize("hasRole('STUDENT')")
	public ResponseEntity<StudentAnswerResponse> submitAnswer(
			@PathVariable Long attemptId,
			@RequestBody SubmitAnswerRequest request,
			@AuthenticationPrincipal User currentUser) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(quizAttemptService.submitAnswer(attemptId, request, currentUser));
	}

	@PostMapping("/attempt/{attemptId}/submit")
	@PreAuthorize("hasRole('STUDENT')")
	public ResponseEntity<QuizAttemptResponse> submitQuizAttempt(
			@PathVariable Long attemptId,
			@AuthenticationPrincipal User currentUser) {

		return ResponseEntity.ok(quizAttemptService.submitQuizAttempt(attemptId, currentUser));
	}

	@GetMapping("/result/{attemptId}")
	@PreAuthorize("hasRole('STUDENT')")
	public ResponseEntity<QuizResultResponse> getQuizResult(
			@PathVariable Long attemptId,
			@AuthenticationPrincipal User currentUser) {

		return ResponseEntity.ok(quizAttemptService.getQuizResult(attemptId, currentUser));
	}

	@GetMapping("/attempts")
	@PreAuthorize("hasRole('STUDENT')")
	public ResponseEntity<Page<QuizAttemptResponse>> getStudentAttempts(
			@AuthenticationPrincipal User currentUser,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		return ResponseEntity.ok(quizAttemptService.getStudentAttempts(currentUser, page, size));
	}

	@GetMapping("/attempts/completed")
	@PreAuthorize("hasRole('STUDENT')")
	public ResponseEntity<List<QuizAttemptResponse>> getCompletedAttempts(
			@AuthenticationPrincipal User currentUser) {

		return ResponseEntity.ok(quizAttemptService.getCompletedAttempts(currentUser));
	}

	@GetMapping("/attempts/incomplete")
	@PreAuthorize("hasRole('STUDENT')")
	public ResponseEntity<List<QuizAttemptResponse>> getIncompleteAttempts(
			@AuthenticationPrincipal User currentUser) {

		return ResponseEntity.ok(quizAttemptService.getIncompleteAttempts(currentUser));
	}
}