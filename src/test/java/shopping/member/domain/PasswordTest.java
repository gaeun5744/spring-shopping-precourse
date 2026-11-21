package shopping.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordTest {

    @ParameterizedTest
    @ValueSource(strings = {"password1", "1234abcd", "Abc12345!"})
    void 영문과_숫자를_포함한_8자_이상이면_생성된다(String value) {
        assertThatCode(() -> new Password(value))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"abc1234", "abcdefgh", "12345678"})
    void 규칙에_맞지_않으면_예외가_발생한다(String value) {
        assertThatThrownBy(() -> new Password(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 암호화된_비밀번호는_원문과_다르고_원문으로_검증된다() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        String encoded = new Password("password1").encode(encoder);

        assertThat(encoded).isNotEqualTo("password1");
        assertThat(encoder.matches("password1", encoded)).isTrue();
    }
}
