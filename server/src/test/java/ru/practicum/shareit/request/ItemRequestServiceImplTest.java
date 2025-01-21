package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.itemDto.ItemInfoForItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemInfo;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.constants.Constants.FORMATTER;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestServiceImplTest {

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    User requester;
    ItemRequestCreateDto itemRequestCreateDto;
    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;

    @BeforeEach
    public void setUp() {
        requester = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        itemRequestCreateDto = new ItemRequestCreateDto("itemRequest_description");

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("itemRequest_description")
                .requester(requester)
                .created(LocalDateTime.now())
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("itemRequest_description")
                .requester(requester)
                .created(FORMATTER.format(itemRequest.getCreated()))
                .build();
    }

    @Test
    public void createItemRequestWhenUserExistsTest() {
        when(userRepository.findById(requester.getId()))
                .thenReturn(Optional.of(requester));
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.createItemRequest(itemRequestCreateDto, requester.getId());

        assertNotNull(result);
        assertEquals(itemRequestDto, result);
        verify(userRepository, Mockito.times(1)).findById(requester.getId());
        verify(itemRequestRepository, Mockito.times(1)).save(any(ItemRequest.class));
    }

    @Test
    void createItemRequestWhenUserNotFoundTest() {
        when(userRepository.findById(requester.getId()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.createItemRequest(itemRequestCreateDto, requester.getId()));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        assertTrue(exception.getMessage().contains(String.valueOf(requester.getId())));
        verify(userRepository, Mockito.times(1)).findById(requester.getId());
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    public void getAllItemRequestsTest() {
        long userId = 2L;

        when(itemRequestRepository.findAll(Sort.by(Sort.Direction.DESC, "created")))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> result = itemRequestService.getAllItemRequests(userId);

        assertNotNull(result);
        assertEquals(List.of(itemRequestDto), result);
    }

    @Test
    public void getItemRequestByIdWhenItemRequestExistsTest() {
        User owner  = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@mail.ru")
                .build();

        Item firstItem = Item.builder()
                .id(1L)
                .name("item1")
                .description("item1_description")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();

        Item secondItem = Item.builder()
                .id(2L)
                .name("item2")
                .description("item2_description")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();

        ItemInfoForItemRequest firstItemInfoForItemRequest = ItemInfoForItemRequest.builder()
                .id(1L)
                .name("item1")
                .ownerId(2L)
                .build();

        ItemInfoForItemRequest secondItemInfoForItemRequest = ItemInfoForItemRequest.builder()
                .id(2L)
                .name("item2")
                .ownerId(2L)
                .build();

        List<ItemInfoForItemRequest> items = new ArrayList<>();
        items.add(firstItemInfoForItemRequest);
        items.add(secondItemInfoForItemRequest);

        ItemRequestDtoWithItemInfo itemRequestDtoWithItemInfo = ItemRequestDtoWithItemInfo.builder()
                .id(1L)
                .description("itemRequest_description")
                .created(FORMATTER.format(itemRequest.getCreated()))
                .items(items)
                .build();

        when(itemRequestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.findAllItemsByRequestId(itemRequest.getId()))
                .thenReturn(List.of(firstItem, secondItem));

        ItemRequestDtoWithItemInfo result = itemRequestService.getItemRequestById(requester.getId());

        assertNotNull(result);
        assertEquals(itemRequestDtoWithItemInfo, result);
        verify(itemRequestRepository, Mockito.times(1)).findById(itemRequest.getId());
        verify(itemRepository, Mockito.times(1)).findAllItemsByRequestId(itemRequest.getId());
    }

    @Test
    void getItemRequestByIdWhenItemRequestNotFoundTest() {
        when(itemRequestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(itemRequest.getId()));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        assertTrue(exception.getMessage().contains(String.valueOf(itemRequest.getId())));
        verify(itemRequestRepository, Mockito.times(1)).findById(itemRequest.getId());
        verify(itemRepository, never()).findAllItemsByRequestId(itemRequest.getId());
    }
}