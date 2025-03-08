package trex.hackathon.smart_prep.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import trex.hackathon.smart_prep.dto.request.QuestionPaperRequest;
import trex.hackathon.smart_prep.dto.response.QuestionPaperResponse;
import trex.hackathon.smart_prep.model.User;
import trex.hackathon.smart_prep.service.QuestionPaperService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/question-papers")
@RequiredArgsConstructor
public class QuestionPaperController {

	private final QuestionPaperService questionPaperService;

	@PostMapping
	@PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
	public ResponseEntity< QuestionPaperResponse > createQuestionPaper(
			@RequestBody QuestionPaperRequest request,
			@AuthenticationPrincipal User currentUser
			) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(questionPaperService.createQuestionPaper(request, currentUser));
	}

	@GetMapping("/bank/{questionBankId}")
	public ResponseEntity< Page<QuestionPaperResponse> > getQuestionPapersByQuestionBank(
			@PathVariable Long questionBankId,
			@AuthenticationPrincipal User currentUser,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
			) {
		return ResponseEntity.ok(questionPaperService.getQuestionPapersByQuestionBank(questionBankId, currentUser, page,
				size));
	}

	@GetMapping("/bank/{questionBankId}/all")
	public ResponseEntity< List<QuestionPaperResponse> > getAllQuestionPapersByQuestionBank(
			@PathVariable Long questionBankId,
			@AuthenticationPrincipal User currentUser
			) {
		return ResponseEntity.ok(questionPaperService.getAllQuestionPapersByQuestionBank(questionBankId, currentUser));
	}

	@GetMapping("/{id}")
	public ResponseEntity< QuestionPaperResponse > getQuestionPaperById(
			@PathVariable Long id,
			@AuthenticationPrincipal User currentUser
			) {
		return ResponseEntity.ok(questionPaperService.getQuestionPaperById(id, currentUser));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
	public ResponseEntity< QuestionPaperResponse > updateQuestionPaper(
			@PathVariable Long id,
			@RequestBody QuestionPaperRequest request,
			@AuthenticationPrincipal User currentUser
			) {
		return ResponseEntity.ok(questionPaperService.updateQuestionPaper(id, request, currentUser));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
	public ResponseEntity< Map<String, String> > deleteQuestionPaper(
			@PathVariable Long id,
			@AuthenticationPrincipal User currentUser
			) {
		questionPaperService.deleteQuestionPaper(id, currentUser);
		return ResponseEntity.ok(Map.of("message", "Question paper deleted successfully"));
	}

	@PutMapping("/{id}/publish")
	@PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
	public ResponseEntity< QuestionPaperResponse > togglePublishStatus(
			@PathVariable Long id,
			@AuthenticationPrincipal User currentUser
			) {
		return ResponseEntity.ok(questionPaperService.togglePublishStatus(id, currentUser));
	}
}
