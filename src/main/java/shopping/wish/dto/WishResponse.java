package shopping.wish.dto;

import java.util.List;
import shopping.product.domain.Product;
import shopping.wish.domain.Wish;

public record WishResponse(Long id, Long productId, String name, int price, String imageUrl) {

    public static WishResponse from(Wish wish) {
        Product product = wish.getProduct();
        return new WishResponse(
                wish.getId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl()
        );
    }

    public static List<WishResponse> fromAll(List<Wish> wishes) {
        return wishes.stream()
                .map(WishResponse::from)
                .toList();
    }
}
