package document_service.v1.exception;

public class BusinessException extends RuntimeException {


    public BusinessException(String message) {
        super(message);
    }
}
