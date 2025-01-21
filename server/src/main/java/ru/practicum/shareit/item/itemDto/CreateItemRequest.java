package ru.practicum.shareit.item.itemDto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateItemRequest {

    String name;
    String description;
    Boolean available;
    Long requestId;
}