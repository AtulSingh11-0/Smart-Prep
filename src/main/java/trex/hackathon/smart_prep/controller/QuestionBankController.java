package trex.hackathon.smart_prep.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import trex.hackathon.smart_prep.dto.request.QuestionBankRequest;
import trex.hackathon.smart_prep.dto.response.QuestionBankResponse;
import trex.hackathon.smart_prep.model.User;
import trex.hackathon.smart_prep.service.QuestionBankService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/question-banks")
@RequiredArgsConstructor
public class QuestionBankController {

	private final QuestionBankService questionBankService;

	@PostMapping
	@PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
	public ResponseEntity< QuestionBankResponse > createQuestionBank(
			@RequestBody QuestionBankRequest request,
			@AuthenticationPrincipal User currentUser
			) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(questionBankService.createQuestionBank(request, currentUser));
	}

	@GetMapping("/my-banks")
	@PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
	public ResponseEntity< Page<QuestionBankResponse> > getMyQuestionBanks(
			@AuthenticationPrincipal User currentUser,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
			) {
		return ResponseEntity.ok(questionBankService.getQuestionBanksByCreator(currentUser, page, size));
	}

	@GetMapping("/my-banks/all")
	@PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
	public ResponseEntity< List<QuestionBankResponse> > getAllMyQuestionBanks(
			@AuthenticationPrincipal User currentUser
			) {
		return ResponseEntity.ok(questionBankService.getAllQuestionBanksByCreator(currentUser));
	}

	@GetMapping("/{id}")
	public ResponseEntity<QuestionBankResponse> getQuestionBankById(
			@PathVariable Long id,
			@AuthenticationPrincipal User currentUser
			) {
		return ResponseEntity.ok(questionBankService.getQuestionBankById(id, currentUser));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
	public ResponseEntity<QuestionBankResponse> updateQuestionBank(
			@PathVariable Long id,
			@RequestBody QuestionBankRequest request,
			@AuthenticationPrincipal User currentUser
			) {
		return ResponseEntity.ok(questionBankService.updateQuestionBank(id, request, currentUser));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
	public ResponseEntity< Map<String, String> > deleteQuestionBank(
			@PathVariable Long id,
			@AuthenticationPrincipal User currentUser
			) {
		questionBankService.deleteQuestionBank(id, currentUser);
		return ResponseEntity.ok(Map.of("message", "Question bank deleted successfully"));
	}
}
