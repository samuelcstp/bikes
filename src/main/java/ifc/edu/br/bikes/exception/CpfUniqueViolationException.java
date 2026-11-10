package ifc.edu.br.bikes.exception;

public class CpfUniqueViolationException extends RuntimeException {
    public CpfUniqueViolationException(String message) {
        super(message);
    }
}
