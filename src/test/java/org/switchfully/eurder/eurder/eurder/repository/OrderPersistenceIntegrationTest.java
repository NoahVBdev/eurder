package org.switchfully.eurder.eurder.eurder.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import org.switchfully.eurder.eurder.common.domain.Address;
import org.switchfully.eurder.eurder.customer.domain.Customer;
import org.switchfully.eurder.eurder.customer.repository.CustomerRepository;
import org.switchfully.eurder.eurder.eurder.api.dto.request.CreateItemGroupDTO;
import org.switchfully.eurder.eurder.eurder.api.dto.request.CreateOrderDTO;
import org.switchfully.eurder.eurder.eurder.domain.ItemGroup;
import org.switchfully.eurder.eurder.eurder.domain.Order;
import org.switchfully.eurder.eurder.eurder.service.OrderService;
import org.switchfully.eurder.eurder.helper.IntegrationTest;
import org.switchfully.eurder.eurder.item.domain.Item;
import org.switchfully.eurder.eurder.item.repository.ItemRepository;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class OrderPersistenceIntegrationTest {
    private static final Address ORIGINAL_ADDRESS = new Address("Rue Neuve", "100B", "1000", "Brussels", "Belgium");
    private static final Address NEW_ADDRESS = new Address("Meir", "1", "2000", "Antwerpen", "Belgium");

    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    TransactionTemplate transactionTemplate;
    @Autowired
    Clock clock;

    @Test
    void givenPlacedOrder_whenReloaded_returnAddressAndPriceSnapshots() {
        // Arrange
        Customer customer = customerRepository.save(
                new Customer("Jane", "Smith", "jane@example.com", ORIGINAL_ADDRESS, "+32425447444", "{noop}password"));
        Item fridge = itemRepository.save(new Item("Fridge", "Cold", new BigDecimal("50.00"), 5));

        UUID orderId = orderService.placeOrder(
                customer.getEmail(),
                new CreateOrderDTO(customer.getId(),
                        List.of(new CreateItemGroupDTO(fridge.getId(), 2))
                )).id();

        // Act: the customer moves and the item gets more expensive, after the order was placed
        customer.changeAddress(NEW_ADDRESS);
        customerRepository.save(customer);
        jdbcTemplate.update("UPDATE eurder.item SET price = 99.00 WHERE id = ?", fridge.getId());

        // Assert: reload inside a transaction, so the lazy item groups can be loaded
        transactionTemplate.executeWithoutResult(status -> {
            Order reloaded = orderRepository.findById(orderId).orElseThrow();

            assertThat(reloaded.getShippingAddress()).isEqualTo(ORIGINAL_ADDRESS);
            assertThat(reloaded.getOrderDate()).isEqualTo(LocalDate.now(clock));
            assertThat(reloaded.getItemGroups()).hasSize(1);

            ItemGroup group = reloaded.getItemGroups().getFirst();
            assertThat(group.getPricePerUnit()).isEqualByComparingTo("50.00");
            assertThat(group.getItemName()).isEqualTo("Fridge");
            assertThat(group.getOrder()).isSameAs(reloaded);
        });
    }
}