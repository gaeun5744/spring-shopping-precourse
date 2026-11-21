package shopping.member.application;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.JwtTokenProvider;
import shopping.auth.UnauthorizedException;
import shopping.member.domain.DuplicateEmailException;
import shopping.member.domain.Email;
import shopping.member.domain.Member;
import shopping.member.domain.MemberRepository;
import shopping.member.domain.Password;
import shopping.member.dto.MemberRequest;
import shopping.member.dto.TokenResponse;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder,
                         JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public TokenResponse register(MemberRequest request) {
        Email email = new Email(request.email());
        Password password = new Password(request.password());
        validateNotDuplicated(email);
        Member member = memberRepository.save(new Member(email, password.encode(passwordEncoder)));
        return new TokenResponse(jwtTokenProvider.createToken(member.getId()));
    }

    public TokenResponse login(MemberRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .filter(found -> found.matchesPassword(request.password(), passwordEncoder))
                .orElseThrow(() -> new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다."));
        return new TokenResponse(jwtTokenProvider.createToken(member.getId()));
    }

    private void validateNotDuplicated(Email email) {
        if (memberRepository.existsByEmail(email.getValue())) {
            throw new DuplicateEmailException("이미 가입된 이메일입니다.");
        }
    }
}
