package net.safedata.reactive.spring.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import net.safedata.reactive.spring.domain.StoreSetup;
import net.safedata.reactive.spring.domain.entity.Product;
import net.safedata.reactive.spring.domain.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class DataInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    private final Random random = new Random(500);

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create();
    }

    @Bean
    public ApplicationRunner applicationRunner(final ReactiveMongoOperations mongoOperations,
                                               final ProductRepository productRepository) {
        return args -> {
            mongoOperations.dropCollection(Product.class)
                           .subscribe();
            mongoOperations.createCollection(Product.class, CollectionOptions.empty()
                                                                             .size(10))
                           .subscribe();

            StoreSetup.getProductNames()
                      .forEach(item -> productRepository.save(new Product(random.nextInt(50), item, 200))
                                                        .subscribe());

            productRepository.count()
                             .doOnNext(value -> LOGGER.info("There are now {} products in the MongoDB database", value))
                             .subscribe();
        };
    }
}
