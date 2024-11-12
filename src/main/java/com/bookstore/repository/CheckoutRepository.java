package com.bookstore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookstore.model.Checkout;

@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, Long>{}
