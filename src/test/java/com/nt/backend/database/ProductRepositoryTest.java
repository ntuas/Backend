package com.nt.backend.database;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void check_insert_product_to_db() {
        // Given is a empty database
        assertThat(productRepository.count()).isEqualTo(0);

        // When there is one item added
        productRepository.save(new Product("Butter"));

        // Then there should be only one item in the database
        assertThat(productRepository.count()).isEqualTo(1);
    }

    @Test
    public void check_deletion_of_one_item_from_db() {
        // Given is a database with three items
        productRepository.save(new Product("Butter"));
        Product milch = productRepository.save(new Product("Milch"));
        productRepository.save(new Product("Wurst"));
        assertThat(productRepository.count()).isEqualTo(3);

        // When the 'Milch'-Entry is deleted
        productRepository.delete(milch);

        // Then only two items must be on the db anymore
        assertThat(productRepository.count()).isEqualTo(2);

        // And there should be only 'Butter' and 'Wurst'
        assertThat(productRepository.findOne("Butter")).isNotNull();
        assertThat(productRepository.findOne("Wurst")).isNotNull();
        assertThat(productRepository.findOne("Milch")).isNull();
    }

    @Test
    public void check_incrementation_of_items_count() {
        // Given one entry in the DB
        Product butter = productRepository.save(new Product("Butter"));
        assertThat(butter.getProductItemsCount()).isEqualTo(0);

        // When items count is incremented by one
        butter.setProductItemsCount(butter.getProductItemsCount() + 1);
        Product newButter = productRepository.findOne("Butter");

        // Then the items on the db must be updated
        assertThat(newButter.getProductItemsCount()).isEqualTo(1);
    }

}