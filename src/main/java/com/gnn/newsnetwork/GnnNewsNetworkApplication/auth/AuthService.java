package com.gnn.newsnetwork.GnnNewsNetworkApplication.auth;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.common.OtpGenerator;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.ForgetPasswordRequestDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.ResetPasswordRequestDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.PasswordResetOtp;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Users;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.UserOtp;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.ROLE;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.repository.PasswordResetOtpRepository;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.repository.UserOtpRepository;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.repository.UserRepository;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenGen authTokenGen;

    private final OtpGenerator otpGenerator;
    private final PasswordResetOtpRepository resetOtpRepository;
    private final EmailService emailServiceImple;
    private final UserOtpRepository otpRepository;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())

        );

        Users users = (Users) authentication.getPrincipal();

        String token = authTokenGen.generateAccessToken(users);

        return new LoginResponseDto(token, users.getId());
    }

    public LoginResponseDto loginUser(UserLoginRequestDto dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        Users users = (Users) authentication.getPrincipal();

        String token = authTokenGen.generateAccessToken(users);

        return new LoginResponseDto(token, users.getId());
    }

    public RegisterResponseDto register(RegisterRequestDto registerRequestDto) throws IOException {
        if(userRepository.findByEmail(registerRequestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Users already exists");
        }

        Users users = Users.builder()
                .name(registerRequestDto.getName())
                .email(registerRequestDto.getEmail())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .phone(registerRequestDto.getPhone())
                .role(ROLE.ADMIN)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(users);
        return RegisterResponseDto.builder()
                .name(users.getName())
                .email(users.getEmail())
                .build();
    }

    public RegisterResponseDto registerEditor(RegisterRequestDto registerRequestDto) throws IOException {
        if(userRepository.findByEmail(registerRequestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Users already exists");
        }

        Users users = Users.builder()
                .name(registerRequestDto.getName())
                .email(registerRequestDto.getEmail())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .phone(registerRequestDto.getPhone())
                .role(ROLE.EDITOR)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(users);
        return RegisterResponseDto.builder()
                .name(users.getName())
                .email(users.getEmail())
                .build();
    }

    public void sendOtp(ForgetPasswordRequestDto dto) {

        Users users = userRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new RuntimeException("No users found"));

        String otp = otpGenerator.generateOtp();

        PasswordResetOtp resetOtp = PasswordResetOtp.builder()
                .email(users.getEmail())
                .otp(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .build();

        System.out.println("AuthService sendOtp() CALLED");
        System.out.println("Email: " + users.getEmail());
        log.info("OTP generated for email={}", users.getEmail());


        resetOtpRepository.save(resetOtp);
        emailServiceImple.sendOtp(users.getEmail(), otp);
    }

    public void resetPassword(ResetPasswordRequestDto dto) throws MessagingException, UnsupportedEncodingException, BadRequestException {

        PasswordResetOtp savedOtp = resetOtpRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new RuntimeException("OTP not found"));

        if(savedOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw  new RuntimeException("OTP expired");
        }

        if(!savedOtp.getOtp().equals(dto.getOtp())) {
            throw new BadRequestException("Invalid OTP");
        }

        Users users = userRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new RuntimeException("Email invalid"));
        users.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(users);

        // Delete the otp for security purpose
        resetOtpRepository.delete(savedOtp);

        try {
            emailServiceImple.sendConfirmationMail(users);
        } catch (Exception e) {
            // optional: log error (don’t fail reset if mail fails)
            System.out.println("Failed to send confirmation email: " + e.getMessage());
        }
    }

    public LoginResponseDto loginOrRegister(UserLoginRequestDto dto) {

        Optional<Users> optionalUser = userRepository.findByUsername(dto.getUsername());

        Users user;

        // 🔹 CASE 1: User exists → Login
        if (optionalUser.isPresent()) {

            user = optionalUser.get();

            // Check password
            if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                throw new RuntimeException("Invalid password");
            }

        }
        // 🔹 CASE 2: User not exists → Register
        else {

            user = Users.builder()
                    .username(dto.getUsername())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .role(ROLE.USER)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            user = userRepository.save(user);
        }

        // 🔹 Generate token
        String token = authTokenGen.generateAccessToken(user);

        return new LoginResponseDto(token, user.getId());
    }


    public UserRegisterResponseDto registerUser(UserRegisterDto dto) throws IOException {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        Users users = Users.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(ROLE.USER)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(users);
        return UserRegisterResponseDto.builder()
                .username(dto.getUsername())
                .build();
    }

//    public void sendRegistrationOtp(UserRegisterDto dto) {
//        // Check whether the provided email exist in DB or not
//        if(userRepository.findByEmail(dto.getEmail()).isPresent()) {
//            throw new IllegalArgumentException("Partner already registered");
//        }
//
//        // Generate otp here
//        String otp = otpGenerator.generateOtp();
//
//        // Creation of otp here ( email, otp, time )
//        UserOtp userOtp = UserOtp.builder()
//                .email(dto.getEmail())
//                .otp(otp)
//                .expiryTime(LocalDateTime.now().plusMinutes(5))
//                .build();
//
//        otpRepository.save(userOtp);
//
//        // Calling emailService to forward otp to delivery partner email
////        emailServiceImple.sendOtp(dto.getEmail(), otp);
//        System.out.println("sendOtp function called : Hheheh");
//        System.out.println("Email: " + dto.getEmail());
//        System.out.println("OTP: " + otp);
//    }

//    @Transactional
//    public LoginResponseDto verifyOtpAndRegister(UserRegisterDto dto, String otp) throws IOException {
//        // Fetch the provided opt here
//        UserOtp savedOtp = otpRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new RuntimeException("Otp not found"));
//
//        // Expiry date validation
//        if (savedOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
//            throw new RuntimeException("Otp expired");
//        }
//
//        // Verifying given otp with Backend provided otp
//        if (!savedOtp.getOtp().equals(otp)) {
//            throw new RuntimeException("Invalid OTP");
//        }
//
//        Users users = userRepository.findByEmail(dto.getEmail())
//                .orElseGet(() -> userRepository.save(
//                        Users.builder()
//                                .email(dto.getEmail())
//                                .role(ROLE.USER)
//                                .active(true)
//                                .createdAt(LocalDateTime.now())
//                                .build()
//                ));
//
//
//        Users savedUsers = userRepository.save(users);
//
//        otpRepository.delete(savedOtp);
//
//        // 3️⃣ Generate JWT using USER
//        String token = authTokenGen.generateAccessToken(savedUsers);
//        return new LoginResponseDto(token, savedUsers.getId());
//    }

}
