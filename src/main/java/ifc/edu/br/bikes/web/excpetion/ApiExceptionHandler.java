package ifc.edu.br.bikes.web.excpetion;

import ifc.edu.br.bikes.exception.CpfUniqueViolationException;
import ifc.edu.br.bikes.exception.EntityNotFoundExcpetion;
import ifc.edu.br.bikes.exception.PasswordInvalidException;
import ifc.edu.br.bikes.exception.UsernameUniqueViolationExcpetion;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> methodArgumentNotValidexcpetion(
            MethodArgumentNotValidException ex,
            HttpServletRequest request,
            BindingResult result){

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.UNPROCESSABLE_CONTENT, "Campo(s) inválido(s)", result));
    }

    @ExceptionHandler({UsernameUniqueViolationExcpetion.class, CpfUniqueViolationException.class})
    public ResponseEntity<ErrorMessage> uniqueViolationException(
            RuntimeException ex,
            HttpServletRequest request){

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundExcpetion.class)
    public ResponseEntity<ErrorMessage> entityNotFoundExcpetion(
            RuntimeException ex,
            HttpServletRequest request){

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    // handler que trata a exceção de senha inválida se a validação não passar
    @ExceptionHandler(PasswordInvalidException.class)
    public ResponseEntity<ErrorMessage> passwordInvalidException(
            RuntimeException ex,
            HttpServletRequest request){

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessage> accessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request){

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.FORBIDDEN, ex.getMessage()));
    }
}
