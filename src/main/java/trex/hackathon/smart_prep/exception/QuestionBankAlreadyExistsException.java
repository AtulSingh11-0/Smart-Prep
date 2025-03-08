package trex.hackathon.smart_prep.exception;

public class QuestionBankAlreadyExistsException extends RuntimeException {
	public QuestionBankAlreadyExistsException (String message) {
		super(message);
	}
}