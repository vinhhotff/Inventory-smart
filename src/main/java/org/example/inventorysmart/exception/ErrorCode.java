package org.example.inventorysmart.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND(1002, "Không tìm thấy sản phẩm", HttpStatus.NOT_FOUND),
    INSUFFICIENT_STOCK(1003, "Số lượng tồn kho không đủ", HttpStatus.BAD_REQUEST);
    ErrorCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
    private final int code;
    private final String message;
    private final HttpStatus statusCode;

}
