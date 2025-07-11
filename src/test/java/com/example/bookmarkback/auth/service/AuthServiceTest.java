package com.example.bookmarkback.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.bookmarkback.auth.dto.LoginRequest;
import com.example.bookmarkback.auth.dto.SignupRequest;
import com.example.bookmarkback.auth.entity.EmailVerification;
import com.example.bookmarkback.auth.repository.EmailVerificationRepository;
import com.example.bookmarkback.global.exception.BadRequestException;
import com.example.bookmarkback.member.dto.MemberResponse;
import com.example.bookmarkback.member.entity.Member;
import com.example.bookmarkback.member.repository.MemberRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"spring.config.location = classpath:test-application.yml"})
@ActiveProfiles("test")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @AfterEach
    void tearDown() {
        emailVerificationRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("메일 인증을 완료한 사용자는 회원가입이 정상적으로 진행된다.")
    void signupTest() throws Exception {
        //given
        SignupRequest signupRequest = getTestSignupRequest("kkk@gmail.com", "포파");
        saveEmailVerification(signupRequest.email(), true);

        //when
        MemberResponse memberResponse = authService.signup(signupRequest);
        Member savedMember = memberRepository.findById(memberResponse.id()).orElse(null);

        //then
        assertThat(memberResponse.id()).isEqualTo(savedMember.getId());
        assertThat(memberResponse.email()).isEqualTo(savedMember.getEmail());
        assertThat(memberResponse.role()).isEqualTo("USER");

    }

    @Test
    @DisplayName("이미 가입된 사용자는 회원 가입을 할 수 없다.")
    void signupDuplicationEmailException() throws Exception {
        SignupRequest signupRequest = getTestSignupRequest("kkk@gmail.com", "포포");
        saveEmailVerification(signupRequest.email(), true);
        authService.signup(signupRequest);

        SignupRequest anotherRequest = getTestSignupRequest("kkk@gmail.com", "포파");
        saveEmailVerification(anotherRequest.email(), true);

        assertThatThrownBy(() -> authService.signup(anotherRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("이미 가입된 이메일이 있습니다.");
    }

    @Test
    @DisplayName("중복된 닉네임을 포함하여 회원가입을 시도하면 예외가 발생한다.")
    void signupDuplicationNicknameException() throws Exception {
        SignupRequest signupRequest = getTestSignupRequest("kkk@gmail.com", "포포");
        saveEmailVerification(signupRequest.email(), true);
        authService.signup(signupRequest);

        SignupRequest anotherRequest = getTestSignupRequest("kksadk@gmail.com", "포포");
        saveEmailVerification(anotherRequest.email(), true);

        assertThatThrownBy(() -> authService.signup(anotherRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("이미 사용중인 닉네임입니다.");
    }

    @Test
    @DisplayName("메일 인증이 되지 않은 사용자는 회원가입시 예외가 발생한다.")
    void signupWithoutMailAuthentication() throws Exception {
        //given
        SignupRequest signupRequest = getTestSignupRequest("kkk@gmail.com", "포파");
        saveEmailVerification(signupRequest.email(), false);

        //when,then
        assertThatThrownBy(() -> authService.signup(signupRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("이메일 인증을 진행하지 않은 사용자입니다.");
    }

    @Test
    @DisplayName("메일 인증 유효 시간이 지나면 회원가입시 예외가 발생한다.")
    void signupAfterValidTime() {
        SignupRequest signupRequest = getTestSignupRequest("kkk@gmail.com", "포파");
        EmailVerification emailVerification = new EmailVerification(signupRequest.email(), "643654", true,
                LocalDateTime.now().plusMinutes(5), LocalDateTime.now());
        emailVerificationRepository.save(emailVerification);

        assertThatThrownBy(() -> authService.signup(signupRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("이메일 인증 유효 시간이 초과되었습니다.");
    }

    @Test
    @DisplayName("로그인을 할 수 있다.")
    public void loginTest() throws Exception {
        SignupRequest signupRequest = getTestSignupRequest("kkk@gmail.com", "포파");
        saveEmailVerification(signupRequest.email(), true);
        MemberResponse signupedMember = authService.signup(signupRequest);

        LoginRequest loginRequest = LoginRequest.builder().email("kkk@gmail.com").password("afkak21@!#2gr").build();
        MemberResponse loginedMember = authService.login(loginRequest);

        assertThat(loginedMember.email()).isEqualTo(signupedMember.email());
        assertThat(loginedMember.accessToken()).isNotNull();
    }

    @Test
    @DisplayName("이메일이 일치하지 않으면 예외가 발생한다.")
    public void loginNotMatchEmail() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder().email("kkk@gmail.com").password("afkak21@!#2gr").build();
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("존재하지 않는 이메일입니다.");
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 예외가 발생한다.")
    public void loginNotMatchPassword() throws Exception {
        SignupRequest signupRequest = getTestSignupRequest("kkk@gmail.com", "포파");
        saveEmailVerification(signupRequest.email(), true);
        authService.signup(signupRequest);

        LoginRequest loginRequest = LoginRequest.builder().email("kkk@gmail.com").password("afgk21@!#2gr").build();
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

    private SignupRequest getTestSignupRequest(String email, String nickname) {
        return SignupRequest.builder()
                .email(email)
                .password("afkak21@!#2gr")
                .name("김철수")
                .nickname(nickname)
                .gender("남자")
                .phoneNumber("01012345678")
                .birthday(LocalDate.of(1900, 12, 21))
                .build();
    }

    private void saveEmailVerification(String email, boolean isVerified) {
        EmailVerification emailVerification = new EmailVerification(email, "643654", isVerified,
                LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusMinutes(5));
        emailVerificationRepository.save(emailVerification);
    }
}