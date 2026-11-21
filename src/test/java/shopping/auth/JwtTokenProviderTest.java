package shopping.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    private static final String SECRET = "test-secret-key-must-be-at-least-32-bytes-long";
    private static final long ONE_HOUR = 3_600_000L;

    private final JwtTokenProvider provider = new JwtTokenProvider(SECRET, ONE_HOUR);

    @Test
    void 토큰에서_회원_id를_꺼낼_수_있다() {
        String token = provider.createToken(1L);

        assertThat(provider.getMemberId(token)).isEqualTo(1L);
    }

    @Test
    void 변조된_토큰이면_예외가_발생한다() {
        String token = provider.createToken(1L);

        assertThatThrownBy(() -> provider.getMemberId(token + "tampered"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void 다른_키로_서명된_토큰이면_예외가_발생한다() {
        JwtTokenProvider otherProvider =
                new JwtTokenProvider("another-secret-key-must-be-at-least-32-bytes", ONE_HOUR);
        String token = otherProvider.createToken(1L);

        assertThatThrownBy(() -> provider.getMemberId(token))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void 만료된_토큰이면_예외가_발생한다() {
        JwtTokenProvider expiredProvider = new JwtTokenProvider(SECRET, -1_000L);
        String token = expiredProvider.createToken(1L);

        assertThatThrownBy(() -> provider.getMemberId(token))
                .isInstanceOf(UnauthorizedException.class);
    }
}
