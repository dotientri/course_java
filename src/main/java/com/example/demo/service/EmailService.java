package com.example.demo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendVerificationOtp(String to, String otp) {
        String subject = "Mã OTP xác thực tài khoản của bạn";
        String title = "Xác thực tài khoản của bạn";
        String body = "Cảm ơn bạn đã đăng ký. Vui lòng sử dụng mã OTP dưới đây để hoàn tất việc xác thực tài khoản. Mã này sẽ hết hạn sau 5 phút.";
        sendHtmlOtpEmail(to, subject, title, body, otp);
    }

    @Async
    public void sendPasswordResetOtp(String to, String otp) {
        String subject = "Mã OTP đặt lại mật khẩu của bạn";
        String title = "Yêu cầu đặt lại mật khẩu";
        String body = "Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng sử dụng mã OTP dưới đây để tiếp tục. Nếu bạn không yêu cầu, vui lòng bỏ qua email này.";
        sendHtmlOtpEmail(to, subject, title, body, otp);
    }

    private void sendHtmlOtpEmail(String to, String subject, String title, String body, String otp) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            // Sử dụng MimeMessageHelper để tạo email HTML, hỗ trợ UTF-8
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            String htmlContent = buildOtpEmailTemplate(title, body, otp);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // 'true' để chỉ định nội dung là HTML

            mailSender.send(mimeMessage);
            log.info("HTML OTP email sent successfully to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to {}: {}", to, e.getMessage());
            // Cân nhắc throw một exception tùy chỉnh ở đây nếu cần
        }
    }

    // Đây là nơi chúng ta xây dựng mẫu email HTML
    private String buildOtpEmailTemplate(String title, String body, String otp) {
        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                    <style>
                        body {
                            font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
                            background-color: #f4f4f7;
                            margin: 0;
                            padding: 0;
                            color: #333;
                        }
                        .container {
                            max-width: 600px;
                            margin: 40px auto;
                            background-color: #ffffff;
                            border-radius: 8px;
                            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
                            overflow: hidden;
                        }
                        .header {
                            background-color: #4A90E2;
                            color: #ffffff;
                            padding: 20px 30px;
                            text-align: center;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 24px;
                        }
                        .content {
                            padding: 30px;
                            line-height: 1.6;
                            text-align: center;
                        }
                        .otp-box {
                            background-color: #f0f4f8;
                            border: 1px dashed #4A90E2;
                            border-radius: 8px;
                            padding: 20px;
                            margin: 20px auto;
                            display: inline-block;
                        }
                        .otp-code {
                            font-size: 36px;
                            font-weight: bold;
                            color: #0d47a1;
                            letter-spacing: 8px; /* Tạo khoảng cách giữa các chữ số */
                            margin: 0;
                        }
                        .footer {
                            background-color: #f4f4f7;
                            color: #888;
                            padding: 20px 30px;
                            text-align: center;
                            font-size: 12px;
                        }
                        .footer p {
                            margin: 0;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>%s</h1>
                        </div>
                        <div class="content">
                            <p>Chào bạn,</p>
                            <p>%s</p>
                            <div class="otp-box">
                                <p class="otp-code">%s</p>
                            </div>
                            <p>Vì lý do bảo mật, vui lòng không chia sẻ mã này với bất kỳ ai.</p>
                        </div>
                        <div class="footer">
                            <p>&copy; %d Your Company. All rights reserved.</p>
                            <p>Đây là email tự động, vui lòng không trả lời.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(title, title, body, otp, java.time.Year.now().getValue());
    }
}