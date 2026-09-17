package com.example.bai2_ss11.service;

import com.example.bai2_ss11.dto.OrderRequest;
import com.example.bai2_ss11.event.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class OrderProducerService {

    private static final Logger log = LoggerFactory.getLogger(OrderProducerService.class);

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final String topicName;

    public OrderProducerService(
            KafkaTemplate<String, OrderEvent> kafkaTemplate,
            @Value("${app.kafka.topic.order-events:storex-order-events}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    /**
     * Tiếp nhận đơn hàng, phát sinh sự kiện order.created vào Kafka và trả về orderId ngay lập tức.
     * 
     * Khắc phục BUG-03:
     * Sử dụng orderId làm Message Key khi gọi kafkaTemplate.send(topic, key, event).
     * Điều này đảm bảo tất cả các sự kiện cùng một orderId luôn được định tuyến vào cùng 1 Partition trong 5 Partitions.
     *
     * @param orderRequest thông tin đơn hàng từ client
     * @return Mono<String> chứa mã đơn hàng orderId
     */
    public Mono<String> createOrder(OrderRequest orderRequest) {
        return Mono.fromCallable(() -> {
            String orderId = (orderRequest != null && orderRequest.orderId() != null && !orderRequest.orderId().isBlank())
                    ? orderRequest.orderId()
                    : UUID.randomUUID().toString();

            OrderRequest requestWithId = new OrderRequest(
                    orderId,
                    orderRequest != null ? orderRequest.customerId() : null,
                    orderRequest != null ? orderRequest.items() : null,
                    orderRequest != null ? orderRequest.totalAmount() : null,
                    orderRequest != null ? orderRequest.shippingAddress() : null
            );

            OrderEvent event = OrderEvent.createOrderCreatedEvent(orderId, requestWithId);

            log.info("Bắt đầu đẩy sự kiện order.created vào Kafka topic '{}' với key (orderId) = '{}'", topicName, orderId);

            // BẮT BUỘC: Sử dụng orderId làm Key phân tuyến (giải quyết BUG-03)
            kafkaTemplate.send(topicName, orderId, event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Thất bại khi gửi sự kiện order.created cho orderId={}: {}", orderId, ex.getMessage(), ex);
                        } else {
                            log.info("Đã gửi thành công sự kiện order.created cho orderId={} vào topic={} | Partition={} | Offset={}",
                                    orderId,
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }
                    });

            return orderId;
        });
    }

    public String getTopicName() {
        return topicName;
    }
}
