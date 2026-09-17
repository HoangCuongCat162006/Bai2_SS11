package com.example.bai2_ss11;

import com.example.bai2_ss11.dto.OrderItemRequest;
import com.example.bai2_ss11.dto.OrderRequest;
import com.example.bai2_ss11.event.OrderEvent;
import com.example.bai2_ss11.service.OrderProducerService;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderProducerServiceTest {

    @Mock
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Captor
    private ArgumentCaptor<String> topicCaptor;

    @Captor
    private ArgumentCaptor<String> keyCaptor;

    @Captor
    private ArgumentCaptor<OrderEvent> eventCaptor;

    private OrderProducerService orderProducerService;

    private final String TOPIC_NAME = "storex-order-events";

    @BeforeEach
    void setUp() {
        orderProducerService = new OrderProducerService(kafkaTemplate, TOPIC_NAME);
    }

    @Test
    @DisplayName("BUG-03 Verification: orderId bắt buộc phải làm khóa (Key) phân tuyến khi gọi kafkaTemplate.send()")
    void shouldSendEventWithOrderIdAsKey() {
        // Given
        String orderId = "ORD-12345";
        OrderRequest request = new OrderRequest(
                orderId,
                "CUST-001",
                List.of(new OrderItemRequest("PROD-1", "Laptop", 1, new BigDecimal("1500.00"))),
                new BigDecimal("1500.00"),
                "123 Nguyen Hue, Quan 1, TP HCM"
        );

        RecordMetadata metadata = new RecordMetadata(new TopicPartition(TOPIC_NAME, 2), 0, 0, 0, 0, 0);
        SendResult<String, OrderEvent> sendResult = new SendResult<>(null, metadata);
        CompletableFuture<SendResult<String, OrderEvent>> future = CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(eq(TOPIC_NAME), eq(orderId), any(OrderEvent.class)))
                .thenReturn(future);

        // When
        var resultMono = orderProducerService.createOrder(request);

        // Then: Mono hoàn thành ngay và trả về orderId
        StepVerifier.create(resultMono)
                .expectNext(orderId)
                .verifyComplete();

        // Verify KafkaTemplate send parameters
        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), eventCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo(TOPIC_NAME);
        // TIÊU CHÍ NGHIỆM THU QUAN TRỌNG: Key phân tuyến phải trùng với orderId
        assertThat(keyCaptor.getValue()).isEqualTo(orderId);

        OrderEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.eventType()).isEqualTo("order.created");
        assertThat(capturedEvent.orderId()).isEqualTo(orderId);
        assertThat(capturedEvent.payload().customerId()).isEqualTo("CUST-001");
        assertThat(capturedEvent.timestamp()).isNotNull();
    }

    @Test
    @DisplayName("Tự động sinh orderId nếu client không truyền và đảm bảo Key phân tuyến bằng chính orderId được sinh ra")
    void shouldGenerateOrderIdWhenMissingAndUseAsKey() {
        // Given
        OrderRequest request = new OrderRequest(
                null, // không truyền orderId
                "CUST-999",
                List.of(),
                BigDecimal.ZERO,
                "HN"
        );

        CompletableFuture<SendResult<String, OrderEvent>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(eq(TOPIC_NAME), any(String.class), any(OrderEvent.class)))
                .thenReturn(future);

        // When
        var resultMono = orderProducerService.createOrder(request);

        // Then
        StepVerifier.create(resultMono)
                .assertNext(generatedId -> {
                    assertThat(generatedId).isNotBlank();
                    // Verify that key equals this generated orderId
                    verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), eventCaptor.capture());
                    assertThat(keyCaptor.getValue()).isEqualTo(generatedId);
                    assertThat(eventCaptor.getValue().orderId()).isEqualTo(generatedId);
                    assertThat(eventCaptor.getValue().eventType()).isEqualTo("order.created");
                })
                .verifyComplete();
    }
}
