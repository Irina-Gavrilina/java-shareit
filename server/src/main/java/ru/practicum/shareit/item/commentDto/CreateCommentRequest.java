package ru.practicum.shareit.item.commentDto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommentRequest {

    private String text;
}