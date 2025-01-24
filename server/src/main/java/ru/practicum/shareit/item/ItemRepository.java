package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(long userId);

    @Query("""
            SELECT i
            FROM Item AS i
            WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', :searchText, '%'))
            OR LOWER(i.description) LIKE LOWER(CONCAT('%', :searchText, '%')))
            AND (:searchText IS NOT NULL AND :searchText != '')
            AND (i.available = TRUE)
            """)
    List<Item> searchItems(String searchText);

    @Query("""
            SELECT i
            FROM Item AS i
            JOIN FETCH i.request AS r
            WHERE i.request.id
            IN :requestIds
            """)
    List<Item> findAllItemsByRequestIdIn(Set<Long> requestIds);

    @Query("""
            SELECT i
            FROM Item AS i
            JOIN FETCH i.request AS r
            WHERE i.request.id = :requestId
            """)
    List<Item> findAllItemsByRequestId(long requestId);
}