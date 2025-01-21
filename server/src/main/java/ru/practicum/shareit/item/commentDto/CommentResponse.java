package ru.practicum.shareit.item.commentDto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {

    Long id;
    String text;
    Long itemId;
    String authorName;
    String created;
}