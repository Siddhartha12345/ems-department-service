package com.department.exception;

import com.department.constant.DepartmentErorEnum;
import com.department.response.BusinessErrorResponse;
import com.department.response.ValidationError;
import com.department.response.ValidationErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BusinessErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {
        BusinessErrorResponse errorResponse = BusinessErrorResponse.builder()
                .errorCode(exception.getErrorCode())
                .errorMessage(exception.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        List<ValidationError> validationErrorList = new ArrayList<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            ValidationError validationError = ValidationError.builder()
                    .errorCode(DepartmentErorEnum.DEPT_VALIDATION_ERR.getErrorCode())
                    .errorMessage(error.getDefaultMessage())
                    .fieldName(((FieldError) error).getField())
                    .build();
            validationErrorList.add(validationError);
        });
        errorResponse.setValidationErrors(validationErrorList);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintException(ConstraintViolationException exception) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        List<ValidationError> validationErrorList = new ArrayList<>();
        for(ConstraintViolation violation : exception.getConstraintViolations()) {
            ValidationError validationError = new ValidationError(DepartmentErorEnum.DEPT_VALIDATION_ERR.getErrorCode(),
                    violation.getPropertyPath().toString(), violation.getMessageTemplate());
            validationErrorList.add(validationError);
        }
        errorResponse.setValidationErrors(validationErrorList);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
