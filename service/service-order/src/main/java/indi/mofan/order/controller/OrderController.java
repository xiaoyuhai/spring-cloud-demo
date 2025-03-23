package indi.mofan.order.controller;


import indi.mofan.order.bean.Order;
import indi.mofan.order.properties.OrderProperties;
import indi.mofan.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mofan
 * @date 2025/3/23 17:34
 */
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderProperties orderProperties;

    @GetMapping("/config")
    public String config() {
        return "order timeout: " + orderProperties.getTimeout()
               + " auto-confirm: " + orderProperties.getAutoConfirm();
    }

    @GetMapping("/create")
    public Order createOrder(@RequestParam("userId") Long userId,
                             @RequestParam("productId") Long productId) {
        return orderService.createOrder(productId, userId);
    }
}
