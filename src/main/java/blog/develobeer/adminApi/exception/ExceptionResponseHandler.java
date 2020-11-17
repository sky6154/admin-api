package blog.develobeer.adminApi.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.NoResultException;
import java.io.IOException;

@ControllerAdvice("blog.develobeer")
public class ExceptionResponseHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity noResultExceptionHandler(UsernameNotFoundException e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity noResultExceptionHandler(NoResultException ne) {
        ne.printStackTrace();
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity exceptionHandler(IOException ioe) {
        ioe.printStackTrace();
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity exceptionHandler(Exception e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().build();
    }
}
