package ru.practicum.shareit.item.commentDto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;
import static ru.practicum.shareit.constants.Constants.FORMATTER;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static CommentResponse toCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemId(comment.getItem().getId())
                .authorName(comment.getAuthor().getName())
                .created(FORMATTER.format(comment.getCreated()))
                .build();
    }

    public static Comment toComment(CreateCommentRequest request, Item item, User author) {
        return Comment.builder()
                .text(request.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }
}