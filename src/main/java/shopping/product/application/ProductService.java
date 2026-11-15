package shopping.product.application;

import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.product.domain.Product;
import shopping.product.domain.ProductRepository;
import shopping.product.dto.ProductCreateRequest;
import shopping.product.dto.ProductResponse;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse create(ProductCreateRequest request) {
        Product product = productRepository.save(request.toEntity());
        return ProductResponse.from(product);
    }

    public ProductResponse findById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다. id=" + productId));
        return ProductResponse.from(product);
    }
}
