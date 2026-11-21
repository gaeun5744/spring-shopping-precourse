package shopping.wish.application;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.ForbiddenException;
import shopping.product.domain.Product;
import shopping.product.domain.ProductRepository;
import shopping.wish.domain.DuplicateWishException;
import shopping.wish.domain.Wish;
import shopping.wish.domain.WishRepository;
import shopping.wish.dto.WishRequest;
import shopping.wish.dto.WishResponse;

@Service
@Transactional(readOnly = true)
public class WishService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    public WishService(WishRepository wishRepository, ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public WishResponse add(Long memberId, WishRequest request) {
        Product product = getProduct(request.productId());
        validateNotDuplicated(memberId, product.getId());
        Wish wish = wishRepository.save(new Wish(memberId, product));
        return WishResponse.from(wish);
    }

    public List<WishResponse> findAll(Long memberId) {
        return WishResponse.fromAll(wishRepository.findAllByMemberId(memberId));
    }

    @Transactional
    public void delete(Long memberId, Long wishId) {
        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new NoSuchElementException("위시를 찾을 수 없습니다. id=" + wishId));
        validateOwner(wish, memberId);
        wishRepository.delete(wish);
    }

    private Product getProduct(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("상품 id는 필수입니다.");
        }
        return productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다. id=" + productId));
    }

    private void validateNotDuplicated(Long memberId, Long productId) {
        if (wishRepository.existsByMemberIdAndProductId(memberId, productId)) {
            throw new DuplicateWishException("이미 위시 리스트에 있는 상품입니다.");
        }
    }

    private void validateOwner(Wish wish, Long memberId) {
        if (!wish.isOwnedBy(memberId)) {
            throw new ForbiddenException("본인의 위시 리스트만 삭제할 수 있습니다.");
        }
    }
}
