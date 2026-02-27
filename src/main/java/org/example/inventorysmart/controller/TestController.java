package org.example.inventorysmart.controller;

import org.example.inventorysmart.dto.response.ApiResponse;
import org.example.inventorysmart.exception.AppException;
import org.example.inventorysmart.exception.ErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test-error")
    public ApiResponse<Void> testError() {
        throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
    }
}
