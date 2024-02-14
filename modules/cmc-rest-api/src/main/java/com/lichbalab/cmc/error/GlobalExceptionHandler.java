package com.lichbalab.cmc.error;

import java.util.UUID;

import com.lichbalab.cmc.core.CmcException;
import com.lichbalab.cmc.core.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

   private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(CmcException.class)
    @ResponseStatus // by default returns 500 error code
    public String handleCmsException(CmcException exception, HttpServletRequest request) {
        String randomString = UUID.randomUUID().toString();
        Object[] params = new Object[]{randomString};
        String userMessage = messageSource.getMessage(exception.getErrorCode().name(), params, request.getLocale());
        log.error(userMessage, exception);
        return userMessage;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus
    public String handleAnyOtherException(Exception exception, HttpServletRequest request) {
        String randomString = UUID.randomUUID().toString();
        Object[] params = new Object[]{randomString};
        String userMessage = messageSource.getMessage(ErrorCode.GENERAL.name(), params, request.getLocale());
        log.error(userMessage, exception);
        return userMessage;
    }
}