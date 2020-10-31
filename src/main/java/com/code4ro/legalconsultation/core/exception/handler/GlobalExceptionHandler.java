package com.code4ro.legalconsultation.core.exception.handler;

import com.code4ro.legalconsultation.core.exception.ExceptionResponse;
import com.code4ro.legalconsultation.core.exception.LegalValidationException;
import com.code4ro.legalconsultation.i18n.model.I18nError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(LegalValidationException.class)
    protected ResponseEntity<Object> handleLegalValidationException(final LegalValidationException ex) {
        final I18nError error = ex.getI18nKey() != null
                ? new I18nError(ex.getI18nKey(), ex.getI8nArguments()) : null;
        return buildResponseEntity(ex.getHttpStatus(), Collections.singletonList(error), ex.getI18nFieldErrors(), null);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(final EntityNotFoundException ex) {
        final I18nError error = new I18nError("validation.Resource.not.found", null);
        return buildResponseEntity(HttpStatus.NOT_FOUND, Collections.singletonList(error), null, ex.getLocalizedMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                  final HttpHeaders headers,
                                                                  final HttpStatus status,
                                                                  final WebRequest request) {
        final Map<String, I18nError> violations = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField,
                        err -> new I18nError(err.getDefaultMessage(), null)));
        return buildResponseEntity(HttpStatus.BAD_REQUEST, null, violations, ex.getLocalizedMessage());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleException(final Exception ex) {
        log.error("Unknown problem.", ex);
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, null, null, ex.getLocalizedMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<Object> handleAccessDenied(final AccessDeniedException ex) {
        final I18nError error = new I18nError("authorization.Access.denied", null);
        return buildResponseEntity(HttpStatus.UNAUTHORIZED, Collections.singletonList(error), null, ex.getLocalizedMessage());
    }

    private ResponseEntity<Object> buildResponseEntity(final HttpStatus httpStatus,
                                                       final List<I18nError> errors,
                                                       final Map<String, I18nError> fieldErrors,
                                                       final String additionalInfo) {
        final ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setI18nErrors(errors);
        exceptionResponse.setI18nFieldErrors(fieldErrors);
        exceptionResponse.setAdditionalInfo(additionalInfo);
        return new ResponseEntity<>(exceptionResponse, httpStatus);
    }
}
