package blog.develobeer.adminApi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.NoResultException;
import java.io.IOException;

@ControllerAdvice("blog.develobeer")
public class ExceptionResponseHandler {

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity noResultExceptionHandler(NoResultException ne){
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity exceptionHandler(IOException ioe){
        ioe.printStackTrace();
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity exceptionHandler(Exception e){
        e.printStackTrace();
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
