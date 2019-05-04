package com.whu.web.config;

import com.whu.common.exception.GlobalException;
import com.whu.common.result.CodeMsg;
import com.whu.common.result.Result;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    @Order(value = 1)
    public Result<String> globalExceptionHandler(RuntimeException e){
        e.printStackTrace();
        try {
            GlobalException ge = (GlobalException) e;
            return Result.error(ge.getCm());
        }
        catch (Exception ex){
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }


    @ExceptionHandler(value=Exception.class)
    @Order(value = 2)
    public Result<String> exceptionHandler(Exception e){
        e.printStackTrace();
        if(e instanceof GlobalException) {
            GlobalException ex = (GlobalException)e;
            return Result.error(ex.getCm());
        }else if(e instanceof BindException) {
            BindException ex = (BindException)e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        }else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
