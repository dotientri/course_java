package com.example.demo.service;

import com.example.demo.configuration.VnPayConfig; // FIX: Changed from .config to .configuration
import com.example.demo.configuration.VnPayProperties; // FIX: Changed from .config to .configuration
import com.example.demo.dto.response.VnPayIpnResponse;
import com.example.demo.exception.AppException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class VnPayService {

    VnPayProperties vnPayProperties;
    OrderService orderService; // Tiêm OrderService để xử lý nghiệp vụ

    public String createPaymentUrl(long orderId, long amount, String orderInfo, HttpServletRequest request) throws UnsupportedEncodingException {
        // ... (phương thức này giữ nguyên, không thay đổi)
        String vnp_TxnRef = String.valueOf(orderId);
        String vnp_IpAddr = VnPayConfig.getIpAddress(request);
        long amountInVND = amount * 100;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnPayProperties.getVersion());
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnPayProperties.getTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(amountInVND));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnPayProperties.getBackendReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VnPayConfig.hmacSHA512(vnPayProperties.getHashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return vnPayProperties.getUrl() + "?" + queryUrl;
    }

    /**
     * Xử lý callback IPN từ VNPay.
     * Đây là nơi chứa logic nghiệp vụ chính để xác nhận thanh toán.
     */
    @Transactional
    public VnPayIpnResponse handleIpn(HttpServletRequest request) {
        try {
            Map<String, String> fields = new HashMap<>();
            for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
                String fieldName = params.nextElement();
                String fieldValue = request.getParameter(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    fields.put(fieldName, fieldValue);
                }
            }

            String vnp_SecureHash = fields.get("vnp_SecureHash");
            if (!isSignatureValid(fields, vnp_SecureHash)) {
                return VnPayIpnResponse.builder().rspCode("97").message("Invalid Checksum").build();
            }

            String vnp_ResponseCode = fields.get("vnp_ResponseCode");
            if (!"00".equals(vnp_ResponseCode)) {
                log.warn("Payment failed for orderId: {}. ResponseCode: {}", fields.get("vnp_TxnRef"), vnp_ResponseCode);
                return VnPayIpnResponse.builder().rspCode(vnp_ResponseCode).message("Payment Failed").build();
            }

            long orderId = Long.parseLong(fields.get("vnp_TxnRef"));
            long amount = Long.parseLong(fields.get("vnp_Amount")) / 100;

            orderService.confirmPayment(orderId, amount);

            return VnPayIpnResponse.builder().rspCode("00").message("Confirm Success").build();

        } catch (AppException e) {
            log.error("Error processing IPN: {}", e.getMessage());
            return VnPayIpnResponse.builder()
                    .rspCode(String.valueOf(e.getErrorCode().getCode()))
                    .message(e.getErrorCode().getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Unknown error processing IPN", e);
            return VnPayIpnResponse.builder().rspCode("99").message("Unknown error").build();
        }
    }

    private boolean isSignatureValid(Map<String, String> fields, String vnp_SecureHash) {
        Map<String, String> fieldsToHash = new HashMap<>(fields);
        fieldsToHash.remove("vnp_SecureHashType");
        fieldsToHash.remove("vnp_SecureHash");
        String signValue = hashAllFields(fieldsToHash);
        return signValue.equals(vnp_SecureHash);
    }

    private String hashAllFields(Map<String, String> fields) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        try {
            for (String fieldName : fieldNames) {
                String fieldValue = fields.get(fieldName);
                if (hashData.length() > 0) {
                    hashData.append('&');
                }
                hashData.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return VnPayConfig.hmacSHA512(vnPayProperties.getHashSecret(), hashData.toString());
    }
}