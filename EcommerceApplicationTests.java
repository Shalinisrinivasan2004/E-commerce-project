package com.portfolio.ecommerce;

import com.portfolio.ecommerce.config.SampleDataLoader;
import com.portfolio.ecommerce.entity.Product;
import com.portfolio.ecommerce.entity.User;
import com.portfolio.ecommerce.repository.ProductRepository;
import com.portfolio.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class EcommerceApplicationTests {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void contextLoadsSeedDataAndRelationships() {
        assertThat(productRepository.count()).isEqualTo(8);
        assertThat(productRepository.search("kettle", null))
                .extracting(Product::getName)
                .contains("Ceramic Pour-Over Kettle");
        assertThat(productRepository.search(null, "Home")).hasSize(2);

        User user = userRepository.findByEmail(SampleDataLoader.DEMO_EMAIL).orElseThrow();
        assertThat(user.getPassword()).doesNotContain(SampleDataLoader.DEMO_PASSWORD);
        assertThat(passwordEncoder.matches(SampleDataLoader.DEMO_PASSWORD, user.getPassword())).isTrue();
        assertThat(user.getCart()).isNotNull();
        assertThat(user.getCart().getUser().getId()).isEqualTo(user.getId());
    }
}
