package com.gnn.newsnetwork.GnnNewsNetworkApplication.service;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Users;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

//@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserRepository userRepository;

    public void sendMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }

    public void sendOtp(String email, String otp) {
        System.out.println("sendOtp function called : Hheheh");
        System.out.println("Email: " + email);
        System.out.println("OTP: " + otp);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset OTP - Food Application");
        message.setText(
                "Your OTP for password reset is: " + otp +
                        "\n\nThis OTP is valid for 5 minutes."
        );
        javaMailSender.send(message);
    }

    public void sendConfirmationMail(Users users) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        messageHelper.setFrom("ffg19162@gmail.com", "Food Application");
        messageHelper.setTo(users.getEmail());
        messageHelper.setSubject("Password reset");

        String msg = """
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #e5e5e5; border-radius: 8px;">
                        <h2 style="color: #2c3e50; text-align: center;">Password Reset Successful</h2>
                
                        <p style="font-size: 14px; color: #333;">
                            Hello <strong>%s</strong>,
                        </p>
                
                        <p style="font-size: 14px; color: #333;">
                            This is a confirmation that your password for your <strong>Food Application</strong> account has been successfully reset.
                        </p>
                
                        <p style="font-size: 14px; color: #333;">
                            If you made this change, no further action is required.
                        </p>
                
                        <p style="font-size: 14px; color: #333;">
                            If you did <strong>not</strong> request a password reset, please contact our support team immediately to secure your account.
                        </p>
                
                        <hr style="margin: 20px 0;" />
                
                        <p style="font-size: 12px; color: #777;">
                            Regards,<br/>
                            <strong>Food Application Team</strong>
                        </p>
                
                        <p style="font-size: 12px; color: #aaa;">
                            This is an automated email. Please do not reply to this message.
                        </p>
                    </div>
                """.formatted(users.getName());


        messageHelper.setText(msg, true);
        javaMailSender.send(mimeMessage);
    }

    public void sendSMS(String phoneNumber, String message) {
        //
    }
}
