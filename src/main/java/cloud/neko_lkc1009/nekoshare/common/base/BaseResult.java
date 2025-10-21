package cloud.neko_lkc1009.nekoshare.common.base;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@Data
@Accessors(chain = true)
public class BaseResult<T> {
    private Integer code;
    private String message;
    private Boolean success;
    private T data;
    private Long timestamp;

    public BaseResult() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> BaseResult<T> success() {
        return new BaseResult<T>().setCode(HttpStatus.OK.value())
                .setMessage(HttpStatus.OK.getReasonPhrase())
                .setSuccess(true);
    }

    public static <T> BaseResult<T> success(T data) {
        return new BaseResult<T>().setCode(HttpStatus.OK.value())
                .setMessage(HttpStatus.OK.getReasonPhrase())
                .setSuccess(true)
                .setData(data);
    }

    public static <T> BaseResult<T> success(String message, T data) {
        return new BaseResult<T>().setCode(HttpStatus.OK.value())
                .setMessage(message)
                .setSuccess(true)
                .setData(data);
    }

    public static <T> BaseResult<T> fail() {
        return new BaseResult<T>().setCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .setSuccess(false);
    }

    public static <T> BaseResult<T> fail(String message) {
        return new BaseResult<T>().setCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setMessage(message)
                .setSuccess(false);
    }

    public static <T> BaseResult<T> fail(Integer code, String message) {
        return new BaseResult<T>().setCode(code)
                .setMessage(message)
                .setSuccess(false);
    }

    public static <T> BaseResult<T> custom(Integer code, String message, Boolean success, T data) {
        return new BaseResult<T>().setCode(code)
                .setMessage(message)
                .setSuccess(success)
                .setData(data);
    }

}
