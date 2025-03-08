package trex.hackathon.smart_prep.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import trex.hackathon.smart_prep.dto.request.QuestionRequest;
import trex.hackathon.smart_prep.dto.response.QuestionResponse;
import trex.hackathon.smart_prep.model.User;
import trex.hackathon.smart_prep.service.QuestionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

	private final QuestionService questionService;

	@PostMapping("/paper/{questionPaperId}")
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	public ResponseEntity< QuestionResponse > createQuestion (
			@PathVariable Long questionPaperId,
			@RequestBody QuestionRequest request,
			@AuthenticationPrincipal User currentUser
			) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(questionService.createQuestion(questionPaperId, request, currentUser));
	}

	@GetMapping("/paper/{questionPaperId}")
	public ResponseEntity< Page<QuestionResponse> > getQuestionsByQuestionPaper (
			@PathVariable Long questionPaperId,
			@AuthenticationPrincipal User currentUser,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
			) {
		return ResponseEntity.ok(questionService.getQuestionsByQuestionPaper(questionPaperId, currentUser, page, size));
	}

	@GetMapping("/paper/{questionPaperId}/all")
	public ResponseEntity< List<QuestionResponse> > getAllQuestionsByQuestionPaper (
			@PathVariable Long questionPaperId,
			@AuthenticationPrincipal User currentUser
			) {
		return ResponseEntity.ok(questionService.getAllQuestionsByQuestionPaper(questionPaperId, currentUser));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	public ResponseEntity<QuestionResponse> getQuestionById (
			@PathVariable Long id,
			@AuthenticationPrincipal User currentUser
			) {
		return ResponseEntity.ok(questionService.getQuestionById(id, currentUser));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	public ResponseEntity<QuestionResponse> updateQuestion (
			@PathVariable Long id,
			@RequestBody QuestionRequest request,
			@AuthenticationPrincipal User currentUser
			) {
		return ResponseEntity.ok(questionService.updateQuestion(id, request, currentUser));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	public ResponseEntity< Map<String, String> > deleteQuestion (
			@PathVariable Long id,
			@AuthenticationPrincipal User currentUser
			) {
		questionService.deleteQuestion(id, currentUser);
		return ResponseEntity.ok(Map.of("message", "Question deleted successfully"));
	}
}
