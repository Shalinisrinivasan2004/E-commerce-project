package com.portfolio.ecommerce.config;

import com.portfolio.ecommerce.entity.Cart;
import com.portfolio.ecommerce.entity.Product;
import com.portfolio.ecommerce.entity.Role;
import com.portfolio.ecommerce.entity.User;
import com.portfolio.ecommerce.repository.ProductRepository;
import com.portfolio.ecommerce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SampleDataLoader implements ApplicationRunner {

    public static final String DEMO_EMAIL = "demo@northmarket.com";
    public static final String DEMO_PASSWORD = "Demo@123";

    private static final Logger log = LoggerFactory.getLogger(SampleDataLoader.class);

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SampleDataLoader(ProductRepository productRepository,
                            UserRepository userRepository,
                            PasswordEncoder passwordEncoder) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (productRepository.count() == 0) {
            seedProducts();
            log.info("Loaded sample products");
        }
        if (userRepository.findByEmail(DEMO_EMAIL).isEmpty()) {
            seedDemoUser();
            log.info("Loaded demo user {}", DEMO_EMAIL);
        }
    }

    private void seedDemoUser() {
        User user = new User();
        user.setName("Demo User");
        user.setEmail(DEMO_EMAIL);
        user.setPassword(passwordEncoder.encode(DEMO_PASSWORD));
        user.setPhone("9876543210");
        user.setAddress("12 Lake Road, Pune, Maharashtra");
        user.setRole(Role.CUSTOMER);

        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);
        userRepository.save(user);
    }

    private void seedProducts() {
        save("Ceramic Pour-Over Kettle", "Kitchen",
                "A 800 ml enamel kettle with a narrow spout for controlled pouring.", "2499.00", 40);
        save("Linen Notebook Set", "Stationery",
                "Two stitched linen notebooks, 96 plain pages each.", "899.00", 80);
        save("Oak Desk Lamp", "Home",
                "A warm LED lamp with a turned oak base and linen shade.", "3299.00", 25);
        save("Merino Wool Scarf", "Apparel",
                "A soft unisex scarf, brushed merino, 180 cm.", "1899.00", 50);
        save("Stainless Water Bottle", "Outdoors",
                "Double-wall steel bottle, 750 ml, leak-resistant cap.", "1299.00", 100);
        save("Wireless Earbuds", "Electronics",
                "In-ear buds with a charging case and USB-C cable.", "4499.00", 30);
        save("Cotton Throw Blanket", "Home",
                "Washed cotton throw, 130 by 180 cm, stone colour.", "2199.00", 35);
        save("Cast Iron Skillet", "Kitchen",
                "26 cm pre-seasoned skillet for stovetop and oven.", "2799.00", 20);
    }

    private void save(String name, String category, String description, String price, int stock) {
        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setDescription(description);
        product.setPrice(new BigDecimal(price));
        product.setStockQuantity(stock);
        productRepository.save(product);
    }
}
