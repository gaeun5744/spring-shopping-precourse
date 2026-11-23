package shopping.product.ui;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopping.product.application.ProductService;
import shopping.product.dto.ProductCreateRequest;
import shopping.product.dto.ProductResponse;
import shopping.product.dto.ProductUpdateRequest;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductCreateRequest request) {
        ProductResponse response = productService.create(request);
        return ResponseEntity.created(URI.create("/api/products/" + response.id()))
                .body(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.findById(productId));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getList() {
        return ResponseEntity.ok(productService.findAll());
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long productId,
            @RequestBody ProductUpdateRequest request
    ) {
        return ResponseEntity.ok(productService.update(productId, request));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(@PathVariable Long productId) {
        productService.delete(productId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> collectionOptions() {
        return ResponseEntity.ok()
                .allow(HttpMethod.GET, HttpMethod.HEAD, HttpMethod.POST, HttpMethod.OPTIONS)
                .build();
    }

    @RequestMapping(value = "/{productId}", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> itemOptions() {
        return ResponseEntity.ok()
                .allow(HttpMethod.GET, HttpMethod.HEAD, HttpMethod.PUT,
                        HttpMethod.DELETE, HttpMethod.OPTIONS)
                .build();
    }
}
