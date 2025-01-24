package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {

    private final UserServiceImpl userService;
    private final EntityManager em;

    @Test
    public void createUserTest() {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .name("user1")
                .email("user1@mail.ru")
                .build();

        userService.createUser(createUserRequest);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);

        User savedUser = query.setParameter("email", createUserRequest.getEmail())
                .getSingleResult();

        assertThat(savedUser.getId(), notNullValue());
        assertThat(savedUser.getName(), equalTo(createUserRequest.getName()));
        assertThat(savedUser.getEmail(), equalTo(createUserRequest.getEmail()));
    }
}
