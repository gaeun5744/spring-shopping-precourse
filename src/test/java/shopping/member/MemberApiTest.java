package shopping.member;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
class MemberApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 회원가입하면_201과_토큰을_반환한다() throws Exception {
        회원가입("register@example.com", "password1")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void 이미_가입된_이메일이면_409를_반환한다() throws Exception {
        회원가입("duplicate@example.com", "password1");

        회원가입("duplicate@example.com", "password2")
                .andExpect(status().isConflict());
    }

    @Test
    void 이메일_형식이_잘못되면_400을_반환한다() throws Exception {
        회원가입("not-an-email", "password1")
                .andExpect(status().isBadRequest());
    }

    @Test
    void 비밀번호_규칙에_맞지_않으면_400을_반환한다() throws Exception {
        회원가입("weak@example.com", "short1")
                .andExpect(status().isBadRequest());
    }

    @Test
    void 올바른_정보로_로그인하면_200과_토큰을_반환한다() throws Exception {
        회원가입("login@example.com", "password1");

        로그인("login@example.com", "password1")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void 비밀번호가_틀리면_401을_반환한다() throws Exception {
        회원가입("wrong@example.com", "password1");

        로그인("wrong@example.com", "password2")
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 가입하지_않은_이메일이면_401을_반환한다() throws Exception {
        로그인("nobody@example.com", "password1")
                .andExpect(status().isUnauthorized());
    }

    private ResultActions 회원가입(String email, String password) throws Exception {
        return 요청("/api/members/register", email, password);
    }

    private ResultActions 로그인(String email, String password) throws Exception {
        return 요청("/api/members/login", email, password);
    }

    private ResultActions 요청(String url, String email, String password) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email": "%s", "password": "%s"}
                        """.formatted(email, password)));
    }
}
