package ru.practicum.shareit.item.itemDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateItemRequest {

    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;
}