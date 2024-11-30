package com.bookstore.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bookstore.model.Checkout;

import java.util.List;

@Repository
public interface CheckoutRepository extends MongoRepository<Checkout, Long>{
    List<Checkout> findByUserId(String userId);
}
