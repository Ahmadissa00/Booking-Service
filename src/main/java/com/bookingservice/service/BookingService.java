package com.bookingservice.service;

import com.bookingservice.client.InventoryServiceClient;
import com.bookingservice.entity.Customer;
import com.bookingservice.event.BookingEvent;
import com.bookingservice.repository.CustomerRepository;
import com.bookingservice.request.BookingRequest;
import com.bookingservice.response.BookingResponse;
import com.bookingservice.response.InventoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class BookingService {

    private final CustomerRepository customerRepository;
    private final InventoryServiceClient inventoryServiceClient;
    private final KafkaTemplate< String,BookingEvent> kafkaTemplate;

    public BookingService(CustomerRepository customerRepository,
                          InventoryServiceClient inventoryServiceClient,
                          KafkaTemplate<String, BookingEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.inventoryServiceClient = inventoryServiceClient;
        this.customerRepository = customerRepository;
    }

    public BookingResponse createBooking(BookingRequest request) {

        final Customer customer = customerRepository.findById(request.getUserId())
                .orElse(null);
        if (customer == null) {
            throw new RuntimeException("Customer not found with ID: " + request.getUserId());
        }

        final InventoryResponse inventoryResponse = inventoryServiceClient.getInventory(request.getEventId());
        log.info("Inventory response for event ID {}: {}", request.getEventId(), inventoryResponse);
        if (inventoryResponse.getCapacity() < request.getTicketCount()){
            throw new RuntimeException("Not enough capacity for event ID: " + request.getEventId());
        }

        final BookingEvent bookingEvent = createBookingEvent(request, customer, inventoryResponse);

        kafkaTemplate.send("booking", bookingEvent);
        log.info("Booking event sent to Kafka: {}", bookingEvent);
        return BookingResponse.builder()
                .userId(bookingEvent.getUserId())
                .eventId(bookingEvent.getEventId())
                .ticketCount(bookingEvent.getTicketCount())
                .totalPrice(bookingEvent.getTotalPrice())
                .build();
    }


    private BookingEvent createBookingEvent(BookingRequest request, Customer customer, InventoryResponse inventoryResponse) {
        return BookingEvent.builder()
                .userId(customer.getId())
                .eventId(request.getEventId())
                .ticketCount(request.getTicketCount())
                .totalPrice(inventoryResponse.getTicketPrice().multiply(BigDecimal.valueOf(request.getTicketCount())))
                .build();
    }


}
