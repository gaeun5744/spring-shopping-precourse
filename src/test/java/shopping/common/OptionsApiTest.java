package shopping.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class OptionsApiTest {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "/api/products       | GET,HEAD,POST,OPTIONS",
            "/api/products/1     | GET,HEAD,PUT,DELETE,OPTIONS",
            "/api/members/register | POST,OPTIONS",
            "/api/members/login  | POST,OPTIONS",
            "/api/wishes         | GET,HEAD,POST,OPTIONS",
            "/api/wishes/1       | DELETE,OPTIONS"
    })
    void OPTIONS_요청은_토큰_없이도_200과_지원하는_메서드를_반환한다(String url, String methods)
            throws Exception {
        MvcResult result = mockMvc.perform(options(url))
                .andExpect(status().isOk())
                .andReturn();

        List<String> allow = Arrays.asList(
                result.getResponse().getHeader(HttpHeaders.ALLOW).split(","));
        assertThat(allow, containsInAnyOrder(methods.split(",")));
    }
}
