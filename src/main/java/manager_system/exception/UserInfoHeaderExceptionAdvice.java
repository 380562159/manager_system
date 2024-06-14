package manager_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserInfoHeaderExceptionAdvice {
    @ExceptionHandler(UserInfoHeaderException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    String userInfoHeaderExceptionHandler(UserInfoHeaderException ex) {return ex.getMessage();}
}
