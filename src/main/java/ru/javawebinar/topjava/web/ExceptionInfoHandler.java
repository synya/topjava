package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.javawebinar.topjava.util.ValidationUtil;
import ru.javawebinar.topjava.util.exception.ErrorInfo;
import ru.javawebinar.topjava.util.exception.ErrorType;
import ru.javawebinar.topjava.util.exception.IllegalRequestDataException;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.getRootCause;
import static ru.javawebinar.topjava.util.exception.ErrorType.*;

@RestControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class ExceptionInfoHandler {
    public static final String DUPLICATE_EMAIL_MESSAGE_CODE = "app.errorDuplicateEmail";
    public static final String DUPLICATE_MEAL_MESSAGE_CODE = "app.errorDuplicateMeal";

    private static Logger log = LoggerFactory.getLogger(ExceptionInfoHandler.class);

    private final MessageSource messageSource;

    @Autowired
    public ExceptionInfoHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    //  http://stackoverflow.com/a/22358422/548473
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(NotFoundException.class)
    public ErrorInfo handleError(HttpServletRequest req, NotFoundException e) {
        return logAndGetErrorInfo(req, e, false, DATA_NOT_FOUND);
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)  // 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorInfo conflict(HttpServletRequest req, DataIntegrityViolationException e) {
        String errorMessage = getRootCause(e).getMessage();
        if (errorMessage.contains("users_unique_email_idx")) {
            return logAndGetErrorInfo(req, e, true, VALIDATION_ERROR,
                    messageSource.getMessage(DUPLICATE_EMAIL_MESSAGE_CODE, null, LocaleContextHolder.getLocale()));
        } else if (errorMessage.contains("meals_unique_user_datetime_idx")) {
            return logAndGetErrorInfo(req, e, true, VALIDATION_ERROR,
                    messageSource.getMessage(DUPLICATE_MEAL_MESSAGE_CODE, null, LocaleContextHolder.getLocale()));
        }
        return logAndGetErrorInfo(req, e, true, DATA_ERROR);
    }

    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)  // 422
    @ExceptionHandler({IllegalRequestDataException.class, MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ErrorInfo illegalRequestDataError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorInfo handleError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, true, APP_ERROR);
    }

    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)  // 422
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public ErrorInfo bindingError(HttpServletRequest req, Exception e) {
        if (e instanceof BindException) {
            return getBindingErrorInfo(req, ((BindException) e).getBindingResult());
        } else if (e instanceof MethodArgumentNotValidException) {
            return getBindingErrorInfo(req, ((MethodArgumentNotValidException) e).getBindingResult());
        } else {
            throw new IllegalArgumentException(e);
        }
    }

    //    https://stackoverflow.com/questions/538870/should-private-helper-methods-be-static-if-they-can-be-static
    private static ErrorInfo logAndGetErrorInfo(HttpServletRequest req, Exception e, boolean logException, ErrorType errorType, String... messages) {
        Throwable rootCause = getRootCause(e);
        if (logException) {
            log.error(errorType + " at request " + req.getRequestURL(), rootCause);
        } else {
            log.warn("{} at request  {}: {}", errorType, req.getRequestURL(), rootCause.toString());
        }
        return new ErrorInfo(req.getRequestURL(), errorType,
                messages.length == 0 ? List.of(ValidationUtil.getMessage(rootCause)) : List.of(messages));
    }

    private static ErrorInfo getBindingErrorInfo(HttpServletRequest req, BindingResult result) {
        ErrorInfo errorInfo = new ErrorInfo(req.getRequestURL(), ErrorType.VALIDATION_ERROR, new ArrayList<>());
        result.getFieldErrors().forEach(
                fe -> {
                    String msg = fe.getDefaultMessage();
                    if (msg != null) {
                        if (!msg.startsWith(fe.getField())) {
                            msg = fe.getField() + ' ' + msg;
                        }
                        errorInfo.addDetail(msg);
                    }
                });
        return errorInfo;
    }
}