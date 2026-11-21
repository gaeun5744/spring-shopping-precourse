package shopping.wish.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import shopping.product.domain.Product;
import shopping.product.domain.ProductName;
import shopping.product.domain.ProductPrice;

class WishTest {

    private final Product product =
            new Product(new ProductName("사과"), new ProductPrice(1000), "https://example.com/a.png");

    @Test
    void 위시를_추가한_회원이면_소유자다() {
        Wish wish = new Wish(1L, product);

        assertThat(wish.isOwnedBy(1L)).isTrue();
    }

    @Test
    void 다른_회원이면_소유자가_아니다() {
        Wish wish = new Wish(1L, product);

        assertThat(wish.isOwnedBy(2L)).isFalse();
    }
}
