package ifc.edu.br.bikes.exception;

public class LocacaoNotFoundException extends RuntimeException {
    public LocacaoNotFoundException(String message) {
        super(message);
    }
}
