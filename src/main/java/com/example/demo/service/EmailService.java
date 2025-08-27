package com.example.demo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {

    JavaMailSender mailSender;

    @NonFinal
    @Value("${app.frontend.url}")
    String frontendUrl;

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

    @Async
    public void sendWelcomeEmailForGoogleUser(String recipientEmail, String recipientName) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            String htmlContent = buildWelcomeEmailTemplate(recipientName, frontendUrl);

            helper.setTo(recipientEmail);
            helper.setSubject("Chào mừng bạn đến với CourseJava Boutique");
            helper.setText(htmlContent, true); // 'true' để chỉ định nội dung là HTML

            mailSender.send(mimeMessage);
            log.info("Sent sophisticated welcome email to new Google user: {}", recipientEmail);

        } catch (MessagingException e) {
            log.error("Failed to send welcome email to {}: {}", recipientEmail, e.getMessage());
        }
    }

    private void sendHtmlOtpEmail(String to, String subject, String title, String body, String otp) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            String htmlContent = buildOtpEmailTemplate(title, body, otp);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("HTML OTP email sent successfully to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to {}: {}", to, e.getMessage());
        }
    }

    // --- TEMPLATE EMAIL CHÀO MỪNG ĐẲNG CẤP THỜI TRANG ---
    // In EmailService.java

    // --- TEMPLATE EMAIL CHÀO MỪNG ĐẲNG CẤP THỜI TRANG ---
    private String buildWelcomeEmailTemplate(String recipientName, String shopUrl) {
        return """
            <!DOCTYPE html>
            <html lang="vi">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Chào mừng đến với CourseJava Boutique</title>
                <style>
                    body {
                        font-family: 'Georgia', 'Times New Roman', serif;
                        background-color: #f5f5f5;
                        margin: 0;
                        padding: 0;
                        color: #333333;
                    }
                    .wrapper {
                        width: 100%%; /* <-- FIXED: Escaped the percent sign */
                        background-color: #f5f5f5;
                        padding: 40px 0;
                    }
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        background-color: #ffffff;
                        border: 1px solid #dddddd;
                        border-radius: 5px;
                        box-shadow: 0 5px 15px rgba(0,0,0,0.05);
                        overflow: hidden;
                    }
                    .header {
                        padding: 40px 30px;
                        text-align: center;
                        border-bottom: 1px solid #eeeeee;
                    }
                    .header img {
                        max-width: 180px;
                    }
                    .content {
                        padding: 40px 30px;
                        text-align: center;
                    }
                    .content h1 {
                        font-size: 28px;
                        font-weight: 300;
                        color: #222222;
                        margin: 0 0 20px;
                    }
                    .content p {
                        font-size: 16px;
                        line-height: 1.7;
                        color: #555555;
                        margin-bottom: 30px;
                    }
                    .cta-button {
                        display: inline-block;
                        background-color: #111111;
                        color: #ffffff;
                        padding: 15px 35px;
                        text-decoration: none;
                        border-radius: 50px;
                        font-size: 16px;
                        font-weight: bold;
                        letter-spacing: 1px;
                        transition: background-color 0.3s;
                    }
                    .footer {
                        padding: 30px;
                        text-align: center;
                        background-color: #fafafa;
                        border-top: 1px solid #eeeeee;
                    }
                    .social-icons a {
                        margin: 0 10px;
                    }
                    .social-icons img {
                        width: 24px;
                        height: 24px;
                    }
                    .footer p {
                        font-size: 12px;
                        color: #999999;
                        margin: 15px 0 0;
                    }
                </style>
            </head>
            <body>
                <div class="wrapper">
                    <div class="container">
                        <div class="header">
                            <!-- THAY THẾ BẰNG URL LOGO CỦA BẠN -->
                            <img src="https://i.imgur.com/sC2n1tH.png" alt="CourseJava Boutique Logo">
                        </div>
                        <div class="content">
                            <h1>Chào mừng, %s!</h1>
                            <p>Cảm ơn bạn đã gia nhập thế giới thời trang của <strong>CourseJava Boutique</strong>. Tài khoản của bạn đã được tạo thành công và một thế giới của phong cách và sự tinh tế đang chờ bạn khám phá.</p>
                            <a href="%s" class="cta-button">KHÁM PHÁ BỘ SƯU TẬP</a>
                        </div>
                        <div class="footer">
                            <div class="social-icons">
                                <!-- THAY THẾ BẰNG LINK MẠNG XÃ HỘI CỦA BẠN -->
                                <a href="#"><img src="https://i.imgur.com/s3mY22V.png" alt="Instagram"></a>
                                <a href="#"><img src="https://i.imgur.com/P3YfO3p.png" alt="Facebook"></a>
                                <a href="#"><img src="https://i.imgur.com/1u1g5kM.png" alt="Pinterest"></a>
                            </div>
                            <p>&copy; %d CourseJava Boutique. All rights reserved.</p>
                            <p>Bạn nhận được email này vì đã đăng ký tài khoản trên website của chúng tôi.</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(recipientName, shopUrl, java.time.Year.now().getValue());
    }

    private String buildOtpEmailTemplate(String title, String body, String otp) {
        // ... (phương thức này giữ nguyên, không thay đổi)
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
                            letter-spacing: 8px;
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