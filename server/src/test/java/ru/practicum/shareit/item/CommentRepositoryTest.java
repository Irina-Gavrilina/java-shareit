package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.commentDto.CommentResponse;
import ru.practicum.shareit.item.commentDto.CreateCommentRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.constants.Constants.FORMATTER;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRepositoryTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    CommentRepository commentRepository;

    @Test
    public void findByItemWithAuthorTest() {
        User owner = User.builder()
                .name("user1")
                .email("user1@mail.ru")
                .build();

        User author = User.builder()
                .name("user2")
                .email("user2@mail.ru")
                .build();

        Item item = Item.builder()
                .name("item1")
                .description("item1_description")
                .available(true)
                .owner(owner)
                .request(null)
                .build();

        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(author)
                .status(BookingStatus.APPROVED)
                .build();

        CreateCommentRequest createCommentRequest = new CreateCommentRequest("Comment");

        Comment comment = Comment.builder()
                .text("Comment")
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        CommentResponse commentResponse = CommentResponse.builder()
                .id(1L)
                .text("Comment")
                .itemId(1L)
                .authorName(author.getName())
                .created(FORMATTER.format(comment.getCreated()))
                .build();

        User savedOwner = userRepository.save(owner);
        User savedAuthor = userRepository.save(author);
        Item savedItem = itemRepository.save(item);
        Booking savedBooking = bookingRepository.save(booking);
        Comment savedComment = commentRepository.save(comment);

        List<Comment> result = commentRepository.findByItemWithAuthor(savedItem);

        assertEquals(1, result.size());
        assertEquals(comment, result.getFirst());
    }
}