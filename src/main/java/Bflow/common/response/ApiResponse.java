package Bflow.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) //If an object is null, it will be ignored in the response
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Instant timestamp;
    private String path;

    //We dont want to use the constructor to have a response standard
    private ApiResponse(
            boolean success,
            String message,
            T data,
            String path
    ) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now();
        this.path = path;
    }

    // ===== SUCCESS =====

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, null, null, null);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    // ===== ERROR =====

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null);
    }

    public static <T> ApiResponse<T> error(String message, String path) {
        return new ApiResponse<>(false, message, null, path);
    }
}