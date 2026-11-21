package shopping.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

class LoginMemberArgumentResolverTest {

    private final JwtTokenProvider provider =
            new JwtTokenProvider("test-secret-key-must-be-at-least-32-bytes-long", 3_600_000L);
    private final LoginMemberArgumentResolver resolver = new LoginMemberArgumentResolver(provider);

    @Test
    void Bearer_토큰이_있으면_회원_id를_반환한다() {
        String token = provider.createToken(7L);

        assertThat(resolver.resolveArgument(null, null, requestWith("Bearer " + token), null))
                .isEqualTo(7L);
    }

    @Test
    void Authorization_헤더가_없으면_예외가_발생한다() {
        assertThatThrownBy(() -> resolver.resolveArgument(null, null, requestWith(null), null))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void Bearer_형식이_아니면_예외가_발생한다() {
        String token = provider.createToken(7L);

        assertThatThrownBy(() -> resolver.resolveArgument(null, null, requestWith(token), null))
                .isInstanceOf(UnauthorizedException.class);
    }

    private ServletWebRequest requestWith(String authorization) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (authorization != null) {
            request.addHeader(HttpHeaders.AUTHORIZATION, authorization);
        }
        return new ServletWebRequest(request);
    }
}
