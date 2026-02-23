package cn.fuguang.account.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.fuguang.exception.ContainerException;
import cn.fuguang.web.BaseResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public BaseResult<Void> handlerNotLoginException(NotLoginException nle) {
        return BaseResult.fail("未登录");
    }

    @ExceptionHandler(ContainerException.class)
    public BaseResult<Void> handlerContainerException(ContainerException e) {
        // Use defineCode if available, otherwise default error code?
        // ContainerException has defineCode.
        return BaseResult.fail(e.getDefineCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public BaseResult<Void> handlerException(Exception e) {
        e.printStackTrace(); // Log stack trace
        return BaseResult.fail(e.getMessage());
    }
}
