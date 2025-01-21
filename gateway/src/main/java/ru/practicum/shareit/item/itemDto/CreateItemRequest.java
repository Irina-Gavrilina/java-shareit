package ru.practicum.shareit.item.itemDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateItemRequest {

    @NotBlank
    @Size(max = 255)
    String name;
    @NotBlank
    @Size(max = 512)
    String description;
    @NotNull
    Boolean available;
    Long requestId;
}