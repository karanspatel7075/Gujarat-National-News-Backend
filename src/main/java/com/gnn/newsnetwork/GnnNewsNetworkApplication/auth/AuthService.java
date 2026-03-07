package com.gnn.newsnetwork.GnnNewsNetworkApplication.auth;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.common.OtpGenerator;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Users;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.ROLE;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.exception.InvalidRequestException;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.repository.PasswordResetOtpRepository;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.repository.UserOtpRepository;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenGen authTokenGen;

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

    public LoginResponseDto loginOrRegister(UserLoginRequestDto dto) {

        Optional<Users> optionalUser = userRepository.findByUsername(dto.getUsername());

        Users user;

        // 🔹 CASE 1: User exists → Login
        if (optionalUser.isPresent()) {

            user = optionalUser.get();

            // Check password
            if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                throw new InvalidRequestException("Invalid username or password");
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
}
