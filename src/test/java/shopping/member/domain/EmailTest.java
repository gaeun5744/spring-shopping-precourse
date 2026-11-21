package shopping.member.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class EmailTest {

    @ParameterizedTest
    @ValueSource(strings = {"user@example.com", "first.last+tag@mail.co.kr"})
    void 올바른_형식의_이메일은_생성된다(String value) {
        assertThatCode(() -> new Email(value))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"user", "user@", "@example.com", "user@example", "us er@example.com"})
    void 잘못된_형식의_이메일은_예외가_발생한다(String value) {
        assertThatThrownBy(() -> new Email(value))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
