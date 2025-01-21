package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("""
            SELECT c
            FROM Comment AS c
            JOIN FETCH c.author AS a
            WHERE c.item = :item
            """)
    List<Comment> findByItemWithAuthor(Item item);
}