package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserJsonTest {

    final JacksonTester<CreateUserRequest> createUserRequestJacksonTester;
    final JacksonTester<UpdateUserRequest> updateUserRequestJacksonTester;
    final JacksonTester<UserResponse> userResponseJacksonTester;

    @Test
    void testCreateUserRequest() throws Exception {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .name("user1")
                .email("user1@mail.ru")
                .build();

        JsonContent<CreateUserRequest> result = createUserRequestJacksonTester.write(createUserRequest);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user1");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user1@mail.ru");
    }

    @Test
    void testUpdateUserRequest() throws Exception {
        UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                .name("user2")
                .email("user2@mail.ru")
                .build();

        JsonContent<UpdateUserRequest> result = updateUserRequestJacksonTester.write(updateUserRequest);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user2");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user2@mail.ru");
    }

    @Test
    void testUserResponse() throws Exception {
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        JsonContent<UserResponse> result = userResponseJacksonTester.write(userResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user1");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user1@mail.ru");
    }
}