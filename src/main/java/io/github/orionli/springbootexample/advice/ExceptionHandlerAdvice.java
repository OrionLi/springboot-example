package io.github.orionli.springbootexample.advice;

import cn.dev33.satoken.exception.NotLoginException;
import com.google.common.base.Throwables;
import io.github.orionli.springbootexample.enums.ResponseEnum;
import io.github.orionli.springbootexample.exception.BizException;
import io.github.orionli.springbootexample.exception.SystemException;
import io.github.orionli.springbootexample.vo.resp.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

/**
 * 拦截异常统一返回
 *
 * @author OrionLi
 * @date 2024/01/07
 */
@Slf4j
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class ExceptionHandlerAdvice {

    /**
     * NotBlank等校验抛出的异常用，只打印第一条信息
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.OK)
    public Result commonResponse(MethodArgumentNotValidException e) {

        return Result.fail(ResponseEnum.CLIENT_BAD_PARAMETERS,
                e.getBindingResult().getAllErrors().getFirst().getDefaultMessage());
    }

    @ExceptionHandler({HandlerMethodValidationException.class})
    @ResponseStatus(HttpStatus.OK)
    public Result commonResponse(HandlerMethodValidationException e) {
        log.error(e.getValueResults().getFirst().getResolvableErrors().getFirst().toString());
        return Result.fail(ResponseEnum.CLIENT_BAD_PARAMETERS,
                e.getValueResults().getFirst().getResolvableErrors().getFirst().getDefaultMessage());
    }

    /**
     * 未登录
     *
     * @return {@link Result}
     */
    @ExceptionHandler({NotLoginException.class})
    @ResponseStatus(HttpStatus.OK)
    public Result commonResponse() {
        return Result.fail(ResponseEnum.NO_LOGIN);
    }

    @ExceptionHandler({BizException.class})
    @ResponseStatus(HttpStatus.OK)
    public Result exceptionResponse(BizException e) {
        log.error(Throwables.getStackTraceAsString(e));
        return Result.fail(e.getResponseEnum(), e.getMessage());
    }

    @ExceptionHandler({SystemException.class})
    @ResponseStatus(HttpStatus.OK)
    public Result exceptionResponse(SystemException e) {
        log.error(Throwables.getStackTraceAsString(e));
        return Result.fail(e.getResponseEnum(), e.getMessage());
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.OK)
    public Result exceptionResponse(Exception e) {
        log.error(Throwables.getStackTraceAsString(e));
        return Result.fail(ResponseEnum.ERROR_500, Throwables.getRootCause(e).getMessage());
    }

}