package com.example.demo.exception;

import com.example.demo.dto.request.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
//khai báo đây là exception

public class GlobalExceptionHandler {
//    cânf khai báo lại class này để spring biết có 1 cái excrption xảy ra thì class này sẽ
//    trách nhiệm handling : bàn giao
//exception nơi  xử lý lỗi
// maintain : duy trì , bảo trì
// exception:ngoại lệ , lỗi
// Validation : xác thực
//    normal respon : phản ứng bình thương giống người dùng có
//    thể tự comsude xử lý được
//    api provider : nhà cung cấp api
//    các nhà api provider sẽ cung cấp api respon ntn

    //bắt lỗi exception
//    @ExceptionHandler(value = RuntimeException.class)
////    nếu xảy ra runtime exception trong system của chúng ta tập trung về đây xử lí
//    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
////        parameter : tham số
//        ApiResponse apiResponse = new ApiResponse();
//        apiResponse.setMessage(exception.getMessage());
//        apiResponse.setCode(1001);
////        thay doi nay thanh ErrorCode trong exception
//        return ResponseEntity.badRequest().body(apiResponse);
////        nêu lỗi trả về 400 là badrequest
//    }
//    Bất kì exception nào xảy ra k trong list bắt
    @ExceptionHandler(value = Exception.class)
//    nếu xảy ra runtime exception trong system của chúng ta tập trung về đây xử lí
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
//        parameter : tham số
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
//        thay doi nay thanh ErrorCode trong exception
        return ResponseEntity.badRequest().body(apiResponse);
//        nêu lỗi trả về 400 là badrequest
    }

    @ExceptionHandler(value = AppException.class)
//    nếu xảy ra runtime exception trong system của chúng ta tập trung về đây xử lí
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
//        parameter : tham số
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(errorCode.getMessage());
        apiResponse.setCode(errorCode.getCode());
//        thay doi nay thanh ErrorCode trong exception
        return ResponseEntity.badRequest().body(apiResponse);
//        nêu lỗi trả về 400 là badrequest
    }

//    @ExceptionHandler(value = MethodArgumentNotValidException.class)
//    ResponseEntity<String> handlingValication(MethodArgumentNotValidException exception) {
//        return ResponseEntity.badRequest().body(exception.getFieldError().getDefaultMessage());
//    } ban dau la nhu the nay
@ExceptionHandler(value = MethodArgumentNotValidException.class)
ResponseEntity<ApiResponse> handlingValication(MethodArgumentNotValidException exception) {
    String enumKey = exception.getFieldError().getDefaultMessage();
    ErrorCode errorCode = ErrorCode.INVALID_KEY;
    try {
        errorCode = ErrorCode.valueOf(enumKey);
    }catch (IllegalArgumentException e) {

    }
    ApiResponse apiResponse = new ApiResponse();
    apiResponse.setMessage(errorCode.getMessage());
    apiResponse.setCode(errorCode.getCode());

    return ResponseEntity.badRequest().body(apiResponse);
}

}
