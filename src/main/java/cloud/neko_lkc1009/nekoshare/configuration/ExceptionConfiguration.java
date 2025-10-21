package cloud.neko_lkc1009.nekoshare.configuration;

import cloud.neko_lkc1009.nekoshare.common.base.BaseResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionConfiguration {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResult<Object>> handleValidationException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn("参数校验异常: URI: {}, Errors: {}", request.getRequestURI(), e.getMessage());

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        BaseResult<Object> result = BaseResult.fail(HttpStatus.BAD_REQUEST.value(), "参数校验失败")
                .setData(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<BaseResult<Object>> handleBindException(
            BindException e, HttpServletRequest request) {
        log.warn("参数绑定异常: URI: {}, Errors: {}", request.getRequestURI(), e.getMessage());

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        BaseResult<Object> result = BaseResult.fail(HttpStatus.BAD_REQUEST.value(), "参数绑定失败")
                .setData(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResult<Object>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.warn("参数类型不匹配: URI: {}, Parameter: {}, Message: {}",
                request.getRequestURI(), e.getName(), e.getMessage());

        String message = null;
        if (e.getRequiredType() != null) {
            message = String.format("参数 '%s' 类型错误，期望类型: %s",
                    e.getName(), e.getRequiredType().getSimpleName());
        }

        BaseResult<Object> result = BaseResult.fail(HttpStatus.BAD_REQUEST.value(), message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<BaseResult<Object>> handleNotFoundException(
            NoHandlerFoundException e, HttpServletRequest request) {
        log.warn("请求路径不存在: URI: {}", request.getRequestURI());

        BaseResult<Object> result = BaseResult.fail(HttpStatus.NOT_FOUND.value(),
                "请求的资源不存在: " + e.getRequestURL());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResult<Object>> handleIllegalArgumentException(
            IllegalArgumentException e, HttpServletRequest request) {
        log.warn("非法参数异常: URI: {}, Message: {}", request.getRequestURI(), e.getMessage());

        BaseResult<Object> result = BaseResult.fail(HttpStatus.BAD_REQUEST.value(),
                e.getMessage() != null ? e.getMessage() : "参数错误");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<BaseResult<Object>> handleNullPointerException(
            NullPointerException e, HttpServletRequest request) {
        log.error("空指针异常: URI: {}", request.getRequestURI(), e);

        BaseResult<Object> result = BaseResult.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "服务器内部错误");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResult<Object>> handleException(Exception e, HttpServletRequest request) {
        log.error("未知异常: URI: {}, Message: {}", request.getRequestURI(), e.getMessage(), e);

        BaseResult<Object> result = BaseResult.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "服务器内部错误，请稍后重试");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

}
