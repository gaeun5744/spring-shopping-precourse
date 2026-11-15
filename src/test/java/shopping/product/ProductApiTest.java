package shopping.product;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ProductApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 상품을_등록하면_201과_등록된_상품을_반환한다() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "[특가] 사과", "price": 1000, "imageUrl": "https://example.com/apple.png"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("[특가] 사과"))
                .andExpect(jsonPath("$.price").value(1000));
    }

    @Test
    void 이름이_잘못되면_400을_반환한다() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "사과!", "price": 1000, "imageUrl": "https://example.com/apple.png"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void 없는_상품을_조회하면_404를_반환한다() throws Exception {
        mockMvc.perform(get("/api/products/{productId}", 999_999))
                .andExpect(status().isNotFound());
    }
}
