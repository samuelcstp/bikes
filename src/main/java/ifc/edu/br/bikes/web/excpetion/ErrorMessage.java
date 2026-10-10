package ifc.edu.br.bikes.web.excpetion;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

public class ErrorMessage {

    private String path;
    private String method;
    private int status;
    private String statutasMessage;
    private String message;

    // Indica que o campo errors será incluído na resposta JSON apenas se não for nulo
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> errors;

    //binding result é um interface que contém os erros de validação
    public ErrorMessage(HttpServletRequest request, HttpStatus status, String message, BindingResult result){
        this.path = request.getRequestURI();
        this.method = request.getMethod();
        this.status = status.value();
        this.statutasMessage = status.getReasonPhrase();
        this.message = message;
        addErrors(result);
    }

    public ErrorMessage(HttpServletRequest request, HttpStatus status, String message){
        this.path = request.getRequestURI();
        this.method = request.getMethod();
        this.status = status.value();
        this.statutasMessage = status.getReasonPhrase();
        this.message = message;
    }

    public void addErrors(BindingResult result) {
        this.errors = new HashMap<>();
        for(FieldError fieldError : result.getFieldErrors()) {
            this.errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
    }
}
