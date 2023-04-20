package org.dominate.achp.sys.handler;

import lombok.extern.slf4j.Slf4j;
import org.dominate.achp.common.enums.ResponseType;
import org.dominate.achp.sys.Response;
import org.dominate.achp.sys.exception.BusinessException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 *
 * @author dominate
 * @since 2022/02/25
 */
@RestControllerAdvice(annotations = {RestController.class})
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 默认统一异常处理方法
     * <p>
     * ExceptionHandler 注解用来配置需要拦截的异常类型, 也可以是自定义异常
     */
    @ExceptionHandler(Exception.class)
    public Response<String> runtimeExceptionHandler(Exception e) {
        log.warn("请求出现异常,异常信息为: {}", e.getMessage(), e);
        return Response.code(ResponseType.SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<String> handleBindException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        if (null == fieldError) {
            return Response.failed();
        }
        log.info("参数校验异常:{}({})", fieldError.getDefaultMessage(), fieldError.getField());
        return Response.error(fieldError.getDefaultMessage());
    }

    @ExceptionHandler(BindException.class)
    public Response<String> handleBindException(BindException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        if (null == fieldError) {
            return Response.failed();
        }
        log.info("参数校验异常:{}({})", fieldError.getDefaultMessage(), fieldError.getField());
        return Response.error(fieldError.getDefaultMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public Response<String> handleBindException(BusinessException ex) {
        return Response.failed(ex);
    }
}
