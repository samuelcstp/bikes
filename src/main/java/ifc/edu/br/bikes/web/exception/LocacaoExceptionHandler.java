package ifc.edu.br.bikes.web.exception;

import ifc.edu.br.bikes.exception.BicicletaIndisponivelException;
import ifc.edu.br.bikes.exception.LocacaoNotFoundException;
import ifc.edu.br.bikes.exception.LocacaoInvalidaException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LocacaoExceptionHandler {

    @ExceptionHandler(BicicletaIndisponivelException.class)
    public ResponseEntity<ErrorMessage> handleBicicletaIndisponivel(BicicletaIndisponivelException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(LocacaoNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleLocacaoNotFound(LocacaoNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(LocacaoInvalidaException.class)
    public ResponseEntity<ErrorMessage> handleLocacaoInvalida(LocacaoInvalidaException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage()));
    }
}
