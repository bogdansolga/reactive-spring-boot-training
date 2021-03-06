package net.safedata.reactive.spring.controller;

import net.safedata.reactive.spring.domain.entity.Product;
import net.safedata.reactive.spring.domain.repository.ProductRepository;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductRepository productRepository;

    @Autowired
    public ProductController(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public Flux<Product> productFlux() {
        return productRepository.findAll();
    }

    @GetMapping("/one")
    public Publisher<Product> justOne() {
        return Mono.just(new Product(20, "Pixel 3", 200));
    }

    @GetMapping("/many")
    public Publisher<Product> many() {
        return Flux.<Product>generate(sink -> sink.next(new Product(5, "iSome", 200)))
                   .take(50);
    }

    @GetMapping(
            value = "/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Publisher<Product> infiniteStreamOfProducts() {
        return Flux.<Product>generate(sink -> sink.next(new Product(10, "The product for " + Instant.now(), 200)))
                   .delayElements(Duration.ofSeconds(1));
    }
}
