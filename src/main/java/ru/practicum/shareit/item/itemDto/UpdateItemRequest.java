package ru.practicum.shareit.item.itemDto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateItemRequest {

    Long id;
    String name;
    String description;
    Boolean available;

    public boolean hasName() {
        return name != null;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public boolean hasAvailable() {
        return available != null;
    }
}