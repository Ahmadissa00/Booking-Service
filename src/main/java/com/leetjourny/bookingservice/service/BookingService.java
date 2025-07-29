package com.leetjourny.bookingservice.service;

import com.leetjourny.bookingservice.client.InventoryServiceClient;
import com.leetjourny.bookingservice.entity.Customer;
import com.leetjourny.bookingservice.repository.CustomerRepository;
import com.leetjourny.bookingservice.request.BookingRequest;
import com.leetjourny.bookingservice.response.BookingResponse;
import com.leetjourny.bookingservice.response.InventoryResponse;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final CustomerRepository customerRepository;
    private final InventoryServiceClient inventoryServiceClient;

    public BookingService(CustomerRepository customerRepository,
                          InventoryServiceClient inventoryServiceClient) {
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
        System.out.println("Inventory Response: " + inventoryResponse);
        if (inventoryResponse.getCapacity() < request.getTicketCount()){
            throw new RuntimeException("Not enough capacity for event ID: " + request.getEventId());
        }





        return BookingResponse.builder().build();
    }


}
