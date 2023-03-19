package com.mastercard.fraud.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public AjaxResponse customException(CustomException e) {
        if(e.getCode() == CustomExceptionType.SYSTEM_ERROR.getCode()){
            log.error(e.getMessage());
        }
        return AjaxResponse.error(e);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public AjaxResponse exception(Exception e) {
        log.error(e.getMessage());
        return AjaxResponse.error(new CustomException(
                CustomExceptionType.OTHER_ERROR, e.getMessage()));
    }
}
