package com.gnn.newsnetwork.GnnNewsNetworkApplication.controller;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.auth.*;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.ForgetPasswordRequestDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.ResetPasswordRequestDto;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Registration API
//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/admin")
    public ResponseEntity<RegisterResponseDto> register(@RequestBody RegisterRequestDto requestDto) throws IOException {
        RegisterResponseDto responseDto = authService.register(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

//    @PreAuthorize("hasRole('EDITOR')")
    @PostMapping("/register/editor")
    public ResponseEntity<RegisterResponseDto> registerEditor(@RequestBody RegisterRequestDto requestDto) throws IOException {
        System.out.println("Users : " + requestDto.getEmail() + "| Password : " + requestDto.getPassword());
        RegisterResponseDto responseDto = authService.registerEditor(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/register/user")
    public ResponseEntity<UserRegisterResponseDto> registerUser(@RequestBody UserRegisterDto requestDto) throws IOException {
        UserRegisterResponseDto responseDto = authService.registerUser(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // Login API
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) throws IOException {
        System.out.println("Users : " + requestDto.getEmail() + "| Password : " + requestDto.getPassword());
        LoginResponseDto loginResponseDto = authService.login(requestDto);
        return ResponseEntity.ok(loginResponseDto);
    }

    // User Login API
    @PostMapping("/user-login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody UserLoginRequestDto dto) throws IOException {
        System.out.println("Users : " + dto.getUsername() + "| Password : " + dto.getPassword());
        LoginResponseDto loginResponseDto = authService.loginUser(dto);
        return ResponseEntity.ok(loginResponseDto);
    }

    @PostMapping("/access")
    public ResponseEntity<LoginResponseDto> loginOrRegister(@RequestBody UserLoginRequestDto dto) {
        return ResponseEntity.ok(authService.loginOrRegister(dto));
    }


//    // Only for Users to login
//    @PostMapping("/register/user/send-otp")
//    public ResponseEntity<String> sendOtp(@ModelAttribute UserRegisterDto dto) {
//        authService.sendRegistrationOtp(dto);
//        return ResponseEntity.ok("OTP sent to email");
//    }
//
//    // Forget Password
//    @PostMapping("/forgot-password")
//    public ResponseEntity<String> forgetPassword(@Valid @RequestBody ForgetPasswordRequestDto dto) {
//        System.out.println("Email : " + dto.getEmail());
//        authService.sendOtp(dto);
//        return ResponseEntity.ok("OTP sent to email");
//    }

    // Reset Password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDto dto) throws MessagingException, UnsupportedEncodingException, BadRequestException {
        System.out.println(dto.getNewPassword() + " " + dto.getEmail() + " " + dto.getOtp());
        authService.resetPassword(dto);
        return ResponseEntity.ok("Password reset successful");
    }

//    @PostMapping(value = "/register/verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<LoginResponseDto> verifyOtp(@ModelAttribute UserRegisterDto dto, @RequestParam String otp) throws IOException {
//        log.info("Received verify OTP request | email={} | otp=[HIDDEN]", dto.getEmail());
//        return ResponseEntity.ok(authService.verifyOtpAndRegister(dto, otp));
//    }

    @PostMapping("/logout") // Learn How is this Working
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.info("User logged out | token={}", token.substring(0, 15) + "...");
        }

//      JWT logout = delete token on frontend
//      Backend logout API is just: for logging
        return ResponseEntity.ok("Logged out successfully");
    }

}
