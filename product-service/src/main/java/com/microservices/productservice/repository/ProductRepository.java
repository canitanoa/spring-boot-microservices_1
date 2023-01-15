package com.microservices.productservice.repository;

import com.microservices.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface ProductRepository extends MongoRepository<Product, String> {
}
