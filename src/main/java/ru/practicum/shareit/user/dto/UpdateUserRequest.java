package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest {

    Long id;
    String name;
    @Email(message = "Неверный формат e-mail")
    String email;

    public boolean hasName() {
        return name != null;
    }

    public boolean hasEmail() {
        return email != null;
    }
}