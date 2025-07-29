package com.leetjourny.bookingservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponse {

    private Long eventId;
    private String event;
    private int capacity;
    private ResponseVenue venue;
    private BigDecimal ticketPrice;
}
