package ifc.edu.br.bikes.exception;

public class UsernameUniqueViolationExcpetion extends RuntimeException {
    public UsernameUniqueViolationExcpetion(String message) {
        super(message);
    }
}
