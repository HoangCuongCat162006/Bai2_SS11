package com.example.bai2_ss11;

import com.example.bai2_ss11.controller.OrderController;
import com.example.bai2_ss11.dto.OrderItemRequest;
import com.example.bai2_ss11.dto.OrderRequest;
import com.example.bai2_ss11.service.OrderProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderProducerService orderProducerService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        OrderController orderController = new OrderController(orderProducerService);
        webTestClient = WebTestClient.bindToController(orderController).build();
    }

    @Test
    @DisplayName("POST /api/v1/orders phải trả về HTTP 202 Accepted và body chứa orderId dạng Mono<String>")
    void shouldReturn202AcceptedWithOrderId() {
        // Given
        String expectedOrderId = "ORD-TEST-9999";
        OrderRequest request = new OrderRequest(
                expectedOrderId,
                "CUST-007",
                List.of(new OrderItemRequest("P01", "Keyboard", 2, new BigDecimal("45.00"))),
                new BigDecimal("90.00"),
                "Hanoi, Vietnam"
        );

        when(orderProducerService.createOrder(any(OrderRequest.class)))
                .thenReturn(Mono.just(expectedOrderId));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isAccepted() // HTTP 202
                .expectBody(String.class)
                .isEqualTo(expectedOrderId);

        verify(orderProducerService).createOrder(any(OrderRequest.class));
    }
}
