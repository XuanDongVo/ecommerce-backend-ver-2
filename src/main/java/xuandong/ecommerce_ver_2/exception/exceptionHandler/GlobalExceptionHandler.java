package xuandong.ecommerce_ver_2.exception.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import xuandong.ecommerce_ver_2.dto.response.RestResponse;
import xuandong.ecommerce_ver_2.exception.IdException;
import xuandong.ecommerce_ver_2.exception.UserAlreadyExistsException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Định nghĩa phương thức xử lý cho ngoại lệ UserAlreadyExistsException
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<RestResponse<Object>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.CONFLICT.value());
        res.setError(ex.getMessage());
        res.setMessage("User already exists.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
    }

    // Định nghĩa phương thức xử lý cho ngoại lệ UsernameNotFoundException
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<RestResponse<Object>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setError(ex.getMessage());
        res.setMessage("Username not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }

    // Định nghĩa phương thức xử lý cho các ngoại lệ chung khác
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse<Object>> handleGeneralException(Exception ex) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        res.setError(ex.getMessage());
        res.setMessage("An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }
    
 // Định nghĩa phương thức xử lý cho ngoại lệ Id 
    @ExceptionHandler(IdException.class)
    public ResponseEntity<RestResponse<Object>> handleNotFoundByIdException(UserAlreadyExistsException ex) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getMessage());
        res.setMessage("An unknown error occurred related to the ID.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
   
}
