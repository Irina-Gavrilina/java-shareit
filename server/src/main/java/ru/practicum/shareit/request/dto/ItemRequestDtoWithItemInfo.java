package ru.practicum.shareit.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.itemDto.ItemInfoForItemRequest;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDtoWithItemInfo {

    Long id;
    String description;
    String created;
    List<ItemInfoForItemRequest> items;
}