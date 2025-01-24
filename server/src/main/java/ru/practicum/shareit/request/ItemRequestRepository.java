package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("""
            SELECT ir
            FROM ItemRequest AS ir
            JOIN FETCH ir.requester AS r
            WHERE ir.requester.id = :userId
            ORDER BY ir.created DESC
            """)
    List<ItemRequest> getAllItemRequestsByUserIdOrderByStartDesc(long userId);
}