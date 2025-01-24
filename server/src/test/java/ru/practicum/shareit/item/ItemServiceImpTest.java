package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortResponse;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.commentDto.CommentResponse;
import ru.practicum.shareit.item.commentDto.CreateCommentRequest;
import ru.practicum.shareit.item.itemDto.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserResponse;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.constants.Constants.FORMATTER;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceImpTest {

    @InjectMocks
    ItemServiceImpl itemService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    CommentRepository commentRepository;

    User owner;
    CreateItemRequest createItemRequest;
    Item item;
    ItemResponse itemResponse;

    @BeforeEach
    public void setUp() {
        owner = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        createItemRequest = CreateItemRequest.builder()
                .name("item1")
                .description("item1_description")
                .available(true)
                .requestId(null)
                .build();

        item = Item.builder()
                .id(1L)
                .name("item1")
                .description("item1_description")
                .available(true)
                .owner(owner)
                .request(null)
                .build();

        itemResponse = ItemResponse.builder()
                .id(1L)
                .name("item1")
                .description("item1_description")
                .available(true)
                .request(null)
                .comments(null)
                .build();
    }

    @Test
    void createItemWithoutItemRequestWhenUserExistsTest() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemResponse result = itemService.createItem(createItemRequest, owner.getId());

        assertNotNull(result);
        assertEquals(itemResponse, result);
        verify(userRepository).findById(anyLong());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItemByItemRequestWhenUserExistsAndItemRequestExistsTest() {
        User requester = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("item_request_description")
                .requester(requester)
                .created(LocalDateTime.now())
                .build();

        CreateItemRequest createItemRequest = CreateItemRequest.builder()
                .name("item1")
                .description("item1_description")
                .available(true)
                .requestId(1L)
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item1")
                .description("item1_description")
                .available(true)
                .request(itemRequest)
                .build();

        ItemResponse itemResponse = ItemResponse.builder()
                .id(1L)
                .name("item1")
                .description("item1_description")
                .available(true)
                .request(1L)
                .comments(null)
                .build();

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemResponse result = itemService.createItem(createItemRequest, owner.getId());

        assertNotNull(result);
        assertEquals(itemResponse, result);
        verify(userRepository).findById(anyLong());
        verify(itemRequestRepository).findById(anyLong());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItemByItemRequestWhenUserExistsAndItemRequestNotFoundTest() {
        User requester = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("item_request_description")
                .requester(requester)
                .created(LocalDateTime.now())
                .build();

        CreateItemRequest createItemRequest = CreateItemRequest.builder()
                .name("item1")
                .description("item1_description")
                .available(true)
                .requestId(1L)
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item1")
                .description("item1_description")
                .available(true)
                .request(itemRequest)
                .build();

        ItemResponse itemResponse = ItemResponse.builder()
                .id(1L)
                .name("item1")
                .description("item1_description")
                .available(true)
                .request(1L)
                .comments(null)
                .build();

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.createItem(createItemRequest, owner.getId()));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        assertTrue(exception.getMessage().contains(String.valueOf(itemRequest.getId())));
        verify(userRepository).findById(anyLong());
        verify(itemRequestRepository, Mockito.times(1)).findById(itemRequest.getId());
        verifyNoInteractions(itemRepository);
    }

    @Test
    void createItemWhenUserNotFoundTest() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.createItem(createItemRequest, owner.getId()));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        assertTrue(exception.getMessage().contains(String.valueOf(owner.getId())));
        verify(userRepository, Mockito.times(1)).findById(owner.getId());
        verifyNoInteractions(itemRepository);
    }

    @Test
    public void getAllItemsByUserIdWhenUserExistsTest() {
        User booker = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@mail.ru")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(2L)
                .name("user2")
                .email("user2@mail.ru")
                .build();

        Item anotherItem = Item.builder()
                .id(2L)
                .name("item2")
                .description("item2_description")
                .available(true)
                .request(null)
                .build();

        Booking bookingOfItem = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        Booking bookingOfAnotherItem = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .item(anotherItem)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        ItemInfoResponse itemInfoResponse = ItemInfoResponse.builder()
                .id(1L)
                .name("item1")
                .description("item1_description")
                .available(true)
                .request(null)
                .lastBooking(new BookingShortResponse(1L, FORMATTER.format(bookingOfItem.getStart()),
                        FORMATTER.format(bookingOfItem.getEnd()), userResponse))
                .nextBooking(null)
                .comments(new ArrayList<>())
                .build();

        ItemInfoResponse anotherItemInfoResponse = ItemInfoResponse.builder()
                .id(2L)
                .name("item2")
                .description("item2_description")
                .available(true)
                .request(null)
                .lastBooking(null)
                .nextBooking(new BookingShortResponse(2L, FORMATTER.format(bookingOfAnotherItem.getStart()),
                        FORMATTER.format(bookingOfAnotherItem.getEnd()), userResponse))
                .comments(new ArrayList<>())
                .build();

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(owner.getId()))
                .thenReturn(List.of(item, anotherItem));
        when(bookingRepository.findByItemIdIn(Set.of(item.getId(), anotherItem.getId())))
                .thenReturn(List.of(bookingOfItem, bookingOfAnotherItem));
        when(commentRepository.findByItemWithAuthor(any(Item.class)))
                .thenReturn(List.of());

        List<ItemInfoResponse> result = itemService.getAllItemsByUserId(owner.getId());

        assertNotNull(result);
        assertEquals(List.of(itemInfoResponse, anotherItemInfoResponse), result);
        verify(userRepository).findById(owner.getId());
        verify(itemRepository).findByOwnerId(owner.getId());
        verify(bookingRepository).findByItemIdIn(anySet());
        verify(commentRepository, Mockito.times(2)).findByItemWithAuthor(any(Item.class));
    }

    @Test
    public void getAllItemsByUserIdWhenUserNotFoundTest() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getAllItemsByUserId(owner.getId()));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        assertTrue(exception.getMessage().contains(String.valueOf(owner.getId())));
        verify(userRepository, Mockito.times(1)).findById(owner.getId());
        verifyNoInteractions(itemRepository);
    }

    @Test
    public void getItemByIdWhenItemExistsTest() {
        ItemInfoResponse itemInfoResponse = ItemInfoResponse.builder()
                .id(1L)
                .name("item1")
                .description("item1_description")
                .available(true)
                .request(null)
                .lastBooking(null)
                .nextBooking(null)
                .comments(new ArrayList<>())
                .build();

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(commentRepository.findByItemWithAuthor(item))
                .thenReturn(List.of());

        ItemInfoResponse result = itemService.getItemById(item.getId());

        assertNotNull(result);
        assertEquals(itemInfoResponse, result);
    }

    @Test
    public void getItemByIdWhenItemNotFoundTest() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getItemById(item.getId()));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        assertTrue(exception.getMessage().contains(String.valueOf(item.getId())));
        verify(itemRepository, Mockito.times(1)).findById(item.getId());
        verifyNoInteractions(commentRepository);
    }

    @Test
    public void searchItemsTest() {
        String searchText = "searchText";

        when(itemRepository.searchItems(searchText))
                .thenReturn(List.of(item));

        List<ItemResponse> result = itemService.searchItems(searchText);

        assertNotNull(result);
        assertEquals(List.of(itemResponse), result);
        verify(itemRepository).searchItems(anyString());
    }

    @Test
    public void searchItemsWhenThereAreNoItemsTest() {
        String searchText = "searchText";

        when(itemRepository.searchItems(searchText))
                .thenReturn(null);

        List<ItemResponse> result = itemService.searchItems(searchText);

        assertNotNull(result);
        assertEquals(List.of(), result);
        verify(itemRepository).searchItems(anyString());
    }

    @Test
    public void updateItemWhenUserExistsAndItemExistsAndUserIsOwnerTest() {
        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .id(1L)
                .name("item1_updated")
                .description("item1_description_updated")
                .available(false)
                .build();

        ItemResponse itemResponse = ItemResponse.builder()
                .id(1L)
                .name("item1_updated")
                .description("item1_description_updated")
                .available(false)
                .request(null)
                .comments(null)
                .build();

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemResponse result = itemService.updateItem(item.getId(), updateItemRequest, owner.getId());

        assertNotNull(result);
        assertEquals(itemResponse, result);
        verify(userRepository).findById(owner.getId());
        verify(itemRepository).findById(item.getId());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    public void updateItemWithEmptyNameTest() {
        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .id(1L)
                .description("item1_description_updated")
                .available(false)
                .build();

        ItemResponse itemResponse = ItemResponse.builder()
                .id(1L)
                .name("item1")
                .description("item1_description_updated")
                .available(false)
                .request(null)
                .comments(null)
                .build();

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemResponse result = itemService.updateItem(item.getId(), updateItemRequest, owner.getId());

        assertNotNull(result);
        assertEquals(itemResponse, result);
        verify(userRepository).findById(owner.getId());
        verify(itemRepository).findById(item.getId());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    public void updateItemWithEmptyDescriptionTest() {
        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .id(1L)
                .name("item1_updated")
                .available(false)
                .build();

        ItemResponse itemResponse = ItemResponse.builder()
                .id(1L)
                .name("item1_updated")
                .description("item1_description")
                .available(false)
                .request(null)
                .comments(null)
                .build();

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemResponse result = itemService.updateItem(item.getId(), updateItemRequest, owner.getId());

        assertNotNull(result);
        assertEquals(itemResponse, result);
        verify(userRepository).findById(owner.getId());
        verify(itemRepository).findById(item.getId());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    public void updateItemWithEmptyAvailableTest() {
        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .id(1L)
                .name("item1_updated")
                .description("item1_description_updated")
                .build();

        ItemResponse itemResponse = ItemResponse.builder()
                .id(1L)
                .name("item1_updated")
                .description("item1_description_updated")
                .available(true)
                .request(null)
                .comments(null)
                .build();

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemResponse result = itemService.updateItem(item.getId(), updateItemRequest, owner.getId());

        assertNotNull(result);
        assertEquals(itemResponse, result);
        verify(userRepository).findById(owner.getId());
        verify(itemRepository).findById(item.getId());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    public void updateItemWhenUserExistsAndItemExistsAndUserIsNotOwnerTest() {
        User user = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@mail.ru")
                .build();

        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .id(1L)
                .name("item1_updated")
                .description("item1_description_updated")
                .available(true)
                .build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> itemService.updateItem(item.getId(), updateItemRequest, user.getId()));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleForbiddenException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        verify(userRepository, Mockito.times(1)).findById(user.getId());
        verify(itemRepository, Mockito.times(1)).findById(item.getId());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    public void updateItemWhenUserNotFoundTest() {
        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .id(1L)
                .name("item1_updated")
                .description("item1_description_updated")
                .available(true)
                .build();

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(item.getId(), updateItemRequest, owner.getId()));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        assertTrue(exception.getMessage().contains(String.valueOf(owner.getId())));
        verify(userRepository, Mockito.times(1)).findById(owner.getId());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    public void updateItemWhenUserExistAndItemNotFoundTest() {
        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .id(1L)
                .name("item1_updated")
                .description("item1_description_updated")
                .available(true)
                .build();

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(item.getId(), updateItemRequest, owner.getId()));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        assertTrue(exception.getMessage().contains(String.valueOf(item.getId())));
        verify(userRepository, Mockito.times(1)).findById(owner.getId());
        verify(itemRepository, Mockito.times(1)).findById(item.getId());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    public void deleteItemByIdTest() {
        itemService.deleteItemById(item.getId());

        verify(itemRepository, Mockito.times(1)).deleteById(item.getId());
    }

    @Test
    public void addCommentWhenAuthorAndItemAndBookingExistAndBookingIsCompletedTest() {
        User author = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@mail.ru")
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(author)
                .status(BookingStatus.APPROVED)
                .build();

        CreateCommentRequest createCommentRequest = new CreateCommentRequest("Comment");

        Comment comment = Comment.builder()
                .id(1L)
                .text("Comment")
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        CommentResponse commentResponse = CommentResponse.builder()
                .id(1L)
                .text("Comment")
                .itemId(1L)
                .authorName(author.getName())
                .created(FORMATTER.format(comment.getCreated()))
                .build();

        when(userRepository.findById(author.getId()))
                .thenReturn(Optional.of(author));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.getPastBookingByBookerIdAndItemId(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentResponse result = itemService.addComment(item.getId(), createCommentRequest, author.getId());

        assertNotNull(result);
        assertEquals(commentResponse, result);
        verify(userRepository, Mockito.times(1)).findById(author.getId());
        verify(itemRepository, Mockito.times(1)).findById(item.getId());
        verify(bookingRepository, Mockito.times(1)).getPastBookingByBookerIdAndItemId(anyLong(), anyLong(),
                any(LocalDateTime.class));
        verify(commentRepository, Mockito.times(1)).save(any(Comment.class));
    }

    @Test
    public void addCommentWhenAuthorNotFoundTest() {
        User author = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@mail.ru")
                .build();

        CreateCommentRequest createCommentRequest = new CreateCommentRequest("Comment");

        when(userRepository.findById(author.getId()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addComment(item.getId(), createCommentRequest, author.getId()));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        assertTrue(exception.getMessage().contains(String.valueOf(author.getId())));
        verify(userRepository, Mockito.times(1)).findById(author.getId());
        verify(itemRepository, never()).findById(anyLong());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    public void addCommentWhenAuthorExistsAndItemNotFoundTest() {
        User author = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@mail.ru")
                .build();

        CreateCommentRequest createCommentRequest = new CreateCommentRequest("Comment");

        when(userRepository.findById(author.getId()))
                .thenReturn(Optional.of(author));

        when((itemRepository.findById(item.getId())))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addComment(item.getId(), createCommentRequest, author.getId()));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        assertTrue(exception.getMessage().contains(String.valueOf(item.getId())));
        verify(userRepository, Mockito.times(1)).findById(author.getId());
        verify(itemRepository, Mockito.times(1)).findById(item.getId());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    public void addCommentWhenAuthorAndItemExistAndBookingNotFoundTest() {
        User author = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@mail.ru")
                .build();

        CreateCommentRequest createCommentRequest = new CreateCommentRequest("Comment");

        when(userRepository.findById(author.getId()))
                .thenReturn(Optional.of(author));

        when((itemRepository.findById(item.getId())))
                .thenReturn(Optional.of(item));
        when(bookingRepository.getPastBookingByBookerIdAndItemId(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        UnavailableBookingException exception = assertThrows(UnavailableBookingException.class,
                () -> itemService.addComment(item.getId(), createCommentRequest, author.getId()));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleUnavailableBookingException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        verify(userRepository, Mockito.times(1)).findById(author.getId());
        verify(itemRepository, Mockito.times(1)).findById(item.getId());
        verify(bookingRepository, Mockito.times(1)).getPastBookingByBookerIdAndItemId(anyLong(), anyLong(), any(LocalDateTime.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }
}