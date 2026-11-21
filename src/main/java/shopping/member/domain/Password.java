package shopping.member.domain;

import java.util.regex.Pattern;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Password {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");

    private final String value;

    public Password(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null || !PASSWORD_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("비밀번호는 영문과 숫자를 포함하여 8자 이상이어야 합니다.");
        }
    }

    public String encode(PasswordEncoder passwordEncoder) {
        return passwordEncoder.encode(value);
    }
}
