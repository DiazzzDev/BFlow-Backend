package Bflow.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.Instant;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final Instant timestamp;
    private final String path;

    private ApiResponse(
            boolean success,
            String message,
            T data,
            Instant timestamp,
            String path
    ) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
        this.path = path;
    }

    // ✅ SUCCESS
    public static <T> ApiResponse<T> success(
            String message,
            T data,
            String path
    ) {
        return new ApiResponse<>(
                true,
                message,
                data,
                Instant.now(),
                path
        );
    }

    // ❌ ERROR
    public static <T> ApiResponse<T> error(
            String message,
            String path
    ) {
        return new ApiResponse<>(
                false,
                message,
                null,
                Instant.now(),
                path
        );
    }
}