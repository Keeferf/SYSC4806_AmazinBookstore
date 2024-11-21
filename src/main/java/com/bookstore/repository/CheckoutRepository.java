package com.bookstore.repository;
import com.bookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookstore.model.Checkout;

import java.util.List;

@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, Long>{
    List<Checkout> findByUser(User user);
}
