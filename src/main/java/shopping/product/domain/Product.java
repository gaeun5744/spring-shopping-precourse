package shopping.product.domain;

public class Product {


    private final ProductName name;
    private final ProductPrice price;
    private final String imageUrl;

    public Product(ProductName name, ProductPrice price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

}
