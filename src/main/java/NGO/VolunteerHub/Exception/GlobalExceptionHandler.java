package NGO.VolunteerHub.Exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - Resource Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                exception.getMessage()
        );
    }

    // 409 - Duplicate Signup
    @ExceptionHandler(DuplicateSignupException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateSignup(
            DuplicateSignupException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                "Duplicate Signup",
                exception.getMessage()
        );
    }

    // 409 - Event Full
    @ExceptionHandler(EventFullException.class)
    public ResponseEntity<Map<String, Object>> handleEventFull(
            EventFullException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                "Event Full",
                exception.getMessage()
        );
    }

    // 409 - Duplicate Attendance
    @ExceptionHandler(DuplicateAttendanceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateAttendance(
            DuplicateAttendanceException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                "Duplicate Attendance",
                exception.getMessage()
        );
    }

    // 400 - Invalid Attendance
    @ExceptionHandler(InvalidAttendanceException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidAttendance(
            InvalidAttendanceException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid Attendance",
                exception.getMessage()
        );
    }

    // 400 - Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException exception) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", 400);
        response.put("error", "Validation Failed");

        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        response.put("messages", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // 400 - Invalid JSON / Invalid data type
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRequestBody(
            HttpMessageNotReadableException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid Request Body",
                "Please check the data types and JSON format of your request"
        );
    }

    // 400 - Missing request parameter
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParameter(
            MissingServletRequestParameterException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Missing Parameter",
                "Required parameter is missing: "
                        + exception.getParameterName()
        );
    }

    // 400 - Wrong parameter type
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid Parameter",
                "Invalid value for parameter: "
                        + exception.getName()
        );
    }

    // 409 - Database constraint violation
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDatabaseError(
            DataIntegrityViolationException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                "Database Constraint Violation",
                "The requested operation violates a database constraint"
        );
    }

    // 500 - Any unexpected error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(
            Exception exception) {

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred"
        );
    }

    // Common response builder
    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status,
            String error,
            String message) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);

        return ResponseEntity
                .status(status)
                .body(response);
    }
}