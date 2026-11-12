package com.rohini.clove.cafe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = "SELECT * FROM orders ORDER BY id LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Order> findChunk(int limit, int offset);
}
