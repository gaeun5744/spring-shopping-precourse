package shopping.wish;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shopping.product.domain.Product;
import shopping.product.domain.ProductName;
import shopping.product.domain.ProductPrice;
import shopping.product.domain.ProductRepository;

@SpringBootTest
@AutoConfigureMockMvc
class WishApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void 위시_리스트에_상품을_추가하면_201과_상품_정보를_반환한다() throws Exception {
        String token = 회원가입();
        Long productId = 상품_생성("사과");

        위시_추가(token, productId)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.name").value("사과"));
    }

    @Test
    void 토큰_없이_요청하면_401을_반환한다() throws Exception {
        mockMvc.perform(get("/api/wishes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 잘못된_토큰으로_요청하면_401을_반환한다() throws Exception {
        위시_조회("invalid-token")
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 없는_상품을_추가하면_404를_반환한다() throws Exception {
        위시_추가(회원가입(), 999_999L)
                .andExpect(status().isNotFound());
    }

    @Test
    void 같은_상품을_두_번_추가하면_409를_반환한다() throws Exception {
        String token = 회원가입();
        Long productId = 상품_생성("바나나");
        위시_추가(token, productId);

        위시_추가(token, productId)
                .andExpect(status().isConflict());
    }

    @Test
    void 내_위시_리스트만_조회된다() throws Exception {
        String me = 회원가입();
        String other = 회원가입();
        위시_추가(me, 상품_생성("포도"));
        위시_추가(other, 상품_생성("딸기"));

        위시_조회(me)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("포도"));
    }

    @Test
    void 위시를_삭제하면_204를_반환하고_목록에서_사라진다() throws Exception {
        String token = 회원가입();
        Long wishId = 위시_id(위시_추가(token, 상품_생성("수박")));

        위시_삭제(token, wishId)
                .andExpect(status().isNoContent());

        위시_조회(token)
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void 다른_회원의_위시를_삭제하면_403을_반환한다() throws Exception {
        String owner = 회원가입();
        String other = 회원가입();
        Long wishId = 위시_id(위시_추가(owner, 상품_생성("참외")));

        위시_삭제(other, wishId)
                .andExpect(status().isForbidden());
    }

    @Test
    void 상품이_삭제되면_위시_리스트에서도_사라진다() throws Exception {
        String token = 회원가입();
        Long productId = 상품_생성("복숭아");
        위시_추가(token, productId);

        mockMvc.perform(delete("/api/products/{productId}", productId))
                .andExpect(status().isNoContent());

        위시_조회(token)
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private String 회원가입() throws Exception {
        String email = UUID.randomUUID() + "@example.com";
        String body = mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "password1"}
                                """.formatted(email)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        return JsonPath.read(body, "$.token");
    }

    private Long 상품_생성(String name) {
        Product product = new Product(
                new ProductName(name), new ProductPrice(1000), "https://example.com/a.png");
        return productRepository.save(product).getId();
    }

    private ResultActions 위시_추가(String token, Long productId) throws Exception {
        return mockMvc.perform(post("/api/wishes")
                .header(HttpHeaders.AUTHORIZATION, bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"productId": %d}
                        """.formatted(productId)));
    }

    private ResultActions 위시_조회(String token) throws Exception {
        return mockMvc.perform(get("/api/wishes")
                .header(HttpHeaders.AUTHORIZATION, bearer(token)));
    }

    private ResultActions 위시_삭제(String token, Long wishId) throws Exception {
        return mockMvc.perform(delete("/api/wishes/{wishId}", wishId)
                .header(HttpHeaders.AUTHORIZATION, bearer(token)));
    }

    private Long 위시_id(ResultActions result) throws Exception {
        String body = result.andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
