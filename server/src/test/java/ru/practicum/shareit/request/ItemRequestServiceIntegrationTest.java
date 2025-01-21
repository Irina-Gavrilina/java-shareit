package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.itemDto.CreateItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestServiceIntegrationTest {

    final ItemRequestServiceImpl itemRequestService;
    final UserServiceImpl userService;
    final ItemServiceImpl itemService;
    final EntityManager em;

    @Test
    public void getAllItemRequestsByUserIdTest() {
        CreateUserRequest createRequesterRequest = new CreateUserRequest("requester", "requester@mail.ru");
        userService.createUser(createRequesterRequest);

        TypedQuery<User> requesterQuery = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User requester = requesterQuery.setParameter("email", createRequesterRequest.getEmail())
                .getSingleResult();

        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("item_request1_description");
        itemRequestService.createItemRequest(itemRequestCreateDto, requester.getId());

        TypedQuery<ItemRequest> requestQuery = em.createQuery("SELECT ir FROM ItemRequest ir WHERE ir.requester =" +
                " :requester", ItemRequest.class);
        ItemRequest itemRequest = requestQuery.setParameter("requester", requester)
                .getSingleResult();

        CreateItemRequest createItemRequest = new CreateItemRequest("item1", "item1_description", true, itemRequest.getId());

        itemRequestService.getAllItemRequestsByUserId(requester.getId());

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestCreateDto.getDescription()));
        assertThat(itemRequest.getRequester(), equalTo(requester));
    }

    @Test
    public void getAllItemRequestsWhenUserNotFoundTest() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllItemRequestsByUserId(1L));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());
    }
}