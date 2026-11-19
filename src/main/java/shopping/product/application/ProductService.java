package shopping.product.application;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.product.domain.Product;
import shopping.product.domain.ProductRepository;
import shopping.product.dto.ProductCreateRequest;
import shopping.product.dto.ProductResponse;
import shopping.product.dto.ProductUpdateRequest;

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
        return ProductResponse.from(getProduct(productId));
    }

    public List<ProductResponse> findAll() {
        List<Product> products = productRepository.findAll();
        return ProductResponse.fromAll(products);
    }

    @Transactional
    public ProductResponse update(Long productId, ProductUpdateRequest request) {
        Product product = getProduct(productId);
        product.update(request.toProductName(), request.toProductPrice(), request.imageUrl());
        return ProductResponse.from(product);
    }

    public void delete(Long productId) {
        Product product = getProduct(productId);
        productRepository.delete(product);
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다. id=" + productId));
    }
}
