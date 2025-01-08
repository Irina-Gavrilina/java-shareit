package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByItemIdAndEndAfterAndStartBefore(long itemId, LocalDateTime start, LocalDateTime end);

    @Query("""
            SELECT b
            FROM Booking AS b
            JOIN FETCH b.item AS i
            WHERE b.id = :bookingId
            """)
    Optional<Booking> findByIdWithItem(long bookingId);

    @Query("""
            SELECT b
            FROM Booking AS b
            JOIN FETCH b.item AS i
            WHERE b.booker.id = :bookerId
            ORDER BY b.start DESC
            """)
    List<Booking> findByBookerIdOrderByStartDesc(long bookerId);

    @Query("""
            SELECT b
            FROM Booking AS b
            JOIN FETCH b.item AS i
            WHERE b.booker.id = :bookerId
            AND b.start <= :start
            AND b.end >= :end
            ORDER BY b.start DESC
            """)
    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long bookerId, LocalDateTime start, LocalDateTime end);

    @Query("""
            SELECT b
            FROM Booking AS b
            JOIN FETCH b.item AS i
            WHERE b.booker.id = :bookerId
            AND b.end <= :now
            ORDER BY b.start DESC
            """)
    List<Booking> findByBookerIdAndAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime now);

    @Query("""
            SELECT b
            FROM Booking AS b
            JOIN FETCH b.item AS i
            WHERE b.booker.id = :bookerId
            AND b.start >= :now
            ORDER BY b.start DESC
            """)
    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime now);

    @Query("""
            SELECT b
            FROM Booking AS b
            JOIN FETCH b.item AS i
            WHERE b.booker.id = :bookerId
            AND b.status = :status
            ORDER BY b.start DESC
            """)
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus status);

    @Query("""
            SELECT b
            FROM Booking AS b
            JOIN FETCH b.item AS i
            WHERE b.item.owner.id = :ownerId
            ORDER BY b.start DESC
            """)
    List<Booking> getAllBookingsByOwnerIdOrderByStartDesc(long ownerId);

    @Query("""
            SELECT b
            FROM Booking AS b
            JOIN FETCH b.item AS i
            WHERE b.item.owner.id = :ownerId
            AND b.start <= :now AND b.end >= :now
            ORDER BY b.start DESC
            """)
    List<Booking> getCurrentBookingsByOwnerIdOrderByStartDesc(long ownerId, LocalDateTime now);

    @Query("""
            SELECT b
            FROM Booking AS b
            JOIN FETCH b.item AS i
            WHERE b.item.owner.id = :ownerId
            AND b.end <= :now
            ORDER BY b.start DESC
            """)
    List<Booking> getPastBookingsByOwnerIdOrderByStartDesc(long ownerId, LocalDateTime now);

    @Query("""
            SELECT b
            FROM Booking AS b
            JOIN FETCH b.item AS i
            WHERE b.item.owner.id = :ownerId
            AND b.start >= :now
            ORDER BY b.start DESC
            """)
    List<Booking> getFutureBookingsByOwnerIdOrderByStartDesc(long ownerId, LocalDateTime now);

    @Query("""
            SELECT b
            FROM Booking AS b
            JOIN FETCH b.item AS i
            WHERE b.item.owner.id = :ownerId
            AND b.status = :status
            ORDER BY b.start DESC
            """)
    List<Booking> getBookingsByOwnerIdAndStatusOrderByStartDesc(long ownerId, BookingStatus status);

    @Query("""
            SELECT b
            FROM Booking AS b
            JOIN FETCH b.item AS i
            WHERE b.item.id
            IN :itemIds
            AND b.status = 'APPROVED'
            ORDER BY b.start DESC
            """)
    List<Booking> findByItemIdIn(Set<Long> itemIds);

    @Query("""
            SELECT b
            FROM Booking AS b
            JOIN FETCH b.item AS i
            WHERE b.item.id = :itemId
            AND b.booker.id = :bookerId
            AND b.end <= :now
            AND b.status = 'APPROVED'
            """)
    Optional<Booking> getPastBookingByBookerIdAndItemId(long itemId, long bookerId, LocalDateTime now);

    List<Booking> findByItem(Item item);
}