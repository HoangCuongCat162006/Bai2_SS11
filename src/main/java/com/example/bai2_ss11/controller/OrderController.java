package com.example.bai2_ss11.controller;

import com.example.bai2_ss11.dto.OrderRequest;
import com.example.bai2_ss11.service.OrderProducerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderProducerService orderProducerService;

    public OrderController(OrderProducerService orderProducerService) {
        this.orderProducerService = orderProducerService;
    }

    /**
     * Tiếp nhận HTTP POST request tại endpoint /api/v1/orders
     * Trả về ngay lập tức HTTP Status 202 Accepted kèm body chứa orderId dưới dạng Mono<String>.
     *
     * @param orderRequest payload JSON chứa thông tin đơn hàng
     * @return Mono<String> mã đơn hàng orderId
     */
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<String> createOrder(@RequestBody(required = false) OrderRequest orderRequest) {
        OrderRequest request = orderRequest != null 
                ? orderRequest 
                : new OrderRequest(null, null, null, null, null);
        return orderProducerService.createOrder(request);
    }
}
