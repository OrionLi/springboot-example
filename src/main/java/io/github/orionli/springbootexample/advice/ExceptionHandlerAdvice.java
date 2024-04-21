package io.github.orionli.careerboost.server.advice;

import cn.dev33.satoken.exception.NotLoginException;
import com.google.common.base.Throwables;
import io.github.orionli.careerboost.common.enums.ResponseEnum;
import io.github.orionli.careerboost.common.exception.BizException;
import io.github.orionli.careerboost.common.exception.SystemException;
import io.github.orionli.careerboost.common.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

/**
 * 拦截异常统一返回
 *
 * @author OrionLi
 * @date 2024/01/07
 */
@Slf4j
@ControllerAdvice(basePackages = "io.github.orionli.careerboost.server.controller")
@ResponseBody
public class ExceptionHandlerAdvice {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.OK)
    public Result commonResponse(MethodArgumentNotValidException e) {
        log.error(Throwables.getRootCause(e).getMessage());
        return Result.fail(ResponseEnum.CLIENT_BAD_PARAMETERS,
                e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler({HandlerMethodValidationException.class})
    @ResponseStatus(HttpStatus.OK)
    public Result commonResponse(HandlerMethodValidationException e) {
        log.error(e.getValueResults().get(0).getResolvableErrors().get(0).toString());
        return Result.fail(ResponseEnum.CLIENT_BAD_PARAMETERS,
                e.getValueResults().get(0).getResolvableErrors().get(0).getDefaultMessage());
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