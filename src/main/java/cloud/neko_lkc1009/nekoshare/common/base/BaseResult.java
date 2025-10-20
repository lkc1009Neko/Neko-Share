package cloud.neko_lkc1009.nekoshare.common.base;

import lombok.Data;
import lombok.experimental.Accessors;

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
        return new BaseResult<T>().setCode(200)
                .setMessage("Success")
                .setSuccess(true);
    }

    public static <T> BaseResult<T> success(T data) {
        return new BaseResult<T>().setCode(200)
                .setMessage("Success")
                .setSuccess(true)
                .setData(data);
    }

    public static <T> BaseResult<T> success(String message, T data) {
        return new BaseResult<T>().setCode(200)
                .setMessage(message)
                .setSuccess(true)
                .setData(data);
    }

    public static <T> BaseResult<T> fail() {
        return new BaseResult<T>().setCode(500)
                .setMessage("fail")
                .setSuccess(false);
    }

    public static <T> BaseResult<T> fail(String message) {
        return new BaseResult<T>().setCode(500)
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
