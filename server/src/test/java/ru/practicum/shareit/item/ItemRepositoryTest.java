package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    User owner;
    User requester;
    ItemRequest firstItemRequest;
    ItemRequest secondItemRequest;
    Item firstItem;
    Item secondItem;
    Item thirdItem;
    Item fourthItem;

    @BeforeEach
    public void setup() {
        owner = User.builder()
                .name("user1")
                .email("user1@mail.ru")
                .build();

        requester = User.builder()
                .name("user2")
                .email("user2@mail.ru")
                .build();

        firstItemRequest = ItemRequest.builder()
                .description("item_request1_description")
                .requester(requester)
                .created(LocalDateTime.now())
                .build();

        secondItemRequest = ItemRequest.builder()
                .description("item_request2_description")
                .requester(requester)
                .created(LocalDateTime.now())
                .build();

        firstItem = Item.builder()
                .name("item1")
                .description("item1_description")
                .available(true)
                .owner(owner)
                .build();

        secondItem = Item.builder()
                .name("item2")
                .description("item2_description")
                .available(true)
                .owner(owner)
                .request(firstItemRequest)
                .build();

        thirdItem = Item.builder()
                .name("item3")
                .description("item3_description")
                .available(true)
                .owner(owner)
                .request(firstItemRequest)
                .build();

        fourthItem = Item.builder()
                .name("item4")
                .description("item4_description")
                .available(true)
                .owner(owner)
                .request(secondItemRequest)
                .build();
    }

    @Test
    public void findByOwnerIdTest() {
        List<Item> items = itemRepository.findAll();

        assertTrue(items.isEmpty());

        User anotherOwner = User.builder()
                .name("user2")
                .email("user2@mail.ru")
                .build();

        Item anotherItem = Item.builder()
                .name("item2nn")
                .description("item2nn_description")
                .available(true)
                .owner(anotherOwner)
                .build();

        User savedOwner = userRepository.save(owner);
        User savedAnotherOwner = userRepository.save(anotherOwner);
        Item savedItem = itemRepository.save(firstItem);
        Item savedAnotherItem = itemRepository.save(anotherItem);
        List<Item> allItems = itemRepository.findAll();

        assertEquals(2, allItems.size());

        List<Item> foundItemsByOwnerId = itemRepository.findByOwnerId(savedOwner.getId());

        assertEquals(1, foundItemsByOwnerId.size());
        assertEquals(savedOwner.getId(), foundItemsByOwnerId.getFirst().getOwner().getId());
    }

    @Test
    public void findAllItemsByRequestIdTest() {
        List<Item> items = itemRepository.findAll();

        assertTrue(items.isEmpty());

        User savedOwner = userRepository.save(owner);
        User savedRequester = userRepository.save(requester);
        ItemRequest savedFirstItemRequest = itemRequestRepository.save(firstItemRequest);
        ItemRequest savedSecondItemRequest = itemRequestRepository.save(secondItemRequest);
        Item savedFirstItem = itemRepository.save(firstItem);
        Item savedSecondItem = itemRepository.save(secondItem);
        Item savedThirdItem = itemRepository.save(thirdItem);
        Item savedFourthItem = itemRepository.save(fourthItem);
        List<Item> foundAllItems = itemRepository.findAll();

        assertEquals(4, foundAllItems.size());

        List<Item> foundItemsByFirstItemRequest = itemRepository.findAllItemsByRequestId(savedFirstItemRequest.getId());

        assertEquals(2, foundItemsByFirstItemRequest.size());
        assertEquals("item2", foundItemsByFirstItemRequest.getFirst().getName());
        assertEquals("item3", foundItemsByFirstItemRequest.get(1).getName());

        List<Item> foundItemsBySecondItemRequest = itemRepository.findAllItemsByRequestId(savedSecondItemRequest.getId());

        assertEquals(1, foundItemsBySecondItemRequest.size());
        assertEquals("item4", foundItemsBySecondItemRequest.getFirst().getName());
    }

    @Test
    public void findAllItemsByRequestIdInTest() {
        List<Item> items = itemRepository.findAll();

        assertTrue(items.isEmpty());

        User savedOwner = userRepository.save(owner);
        User savedRequester = userRepository.save(requester);
        ItemRequest savedFirstItemRequest = itemRequestRepository.save(firstItemRequest);
        ItemRequest savedSecondItemRequest = itemRequestRepository.save(secondItemRequest);
        Item savedFirstItem = itemRepository.save(firstItem);
        Item savedSecondItem = itemRepository.save(secondItem);
        Item savedThirdItem = itemRepository.save(thirdItem);
        Item savedFourthItem = itemRepository.save(fourthItem);
        Set<Long> requestIds = Set.of(savedFirstItemRequest.getId(), savedSecondItemRequest.getId());
        List<Item> foundAllItems = itemRepository.findAll();

        assertEquals(4, foundAllItems.size());

        List<Item> foundItems = itemRepository.findAllItemsByRequestIdIn(requestIds);

        assertEquals(3, foundItems.size());
        assertEquals("item2", foundItems.getFirst().getName());
        assertEquals("item3", foundItems.get(1).getName());
        assertEquals("item4", foundItems.get(2).getName());
    }

    @Test
    public void searchItemsTest() {
        List<Item> items = itemRepository.searchItems("компьютер");

        assertTrue(items.isEmpty());

        Item firstAvailableItem = Item.builder()
                .name("компьютер")
                .description("item1_description")
                .available(true)
                .owner(owner)
                .build();

        Item secondAvailableItem = Item.builder()
                .name("компьютерный стол")
                .description("item1_description")
                .available(true)
                .owner(owner)
                .build();

        Item thirdAnavailableItem = Item.builder()
                .name("компьютерная мышь")
                .description("item1_description")
                .available(false)
                .owner(owner)
                .build();

        List<Item> searchItems = List.of(firstAvailableItem, secondAvailableItem);
        User savedOwner = userRepository.save(owner);
        Item savedFirstAvailableItem = itemRepository.save(firstAvailableItem);
        Item savedSecondAvailableItem = itemRepository.save(secondAvailableItem);
        Item savedThirdAnavailableItem = itemRepository.save(thirdAnavailableItem);
        List<Item> foundItemsFirstScenario = itemRepository.searchItems("КоМп");

        assertEquals(2, foundItemsFirstScenario.size());
        assertTrue(foundItemsFirstScenario.containsAll(searchItems));

        List<Item> foundItemsSecondScenario = itemRepository.searchItems("");

        assertEquals(0, foundItemsSecondScenario.size());
    }
}