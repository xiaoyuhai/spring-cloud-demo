package indi.mofan.order.service;


import indi.mofan.order.bean.Order;
import indi.mofan.product.bean.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author mofan
 * @date 2025/3/23 17:36
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Order createOrder(Long productId, Long userId) {
        Order order = new Order();
        order.setId(productId);
        Product product = getProductFromRemote(productId);
        BigDecimal totalAmount = product.getPrice().multiply(new BigDecimal(product.getNum()));
        order.setTotalAmount(totalAmount);
        order.setUserId(userId);
        order.setNickname("mofan");
        order.setAddress("Chengdu");
        order.setProductList(List.of(product));
        return order;
    }

    private Product getProductFromRemote(Long productId) {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
        ServiceInstance first = instances.getFirst();
        // 远程 url
        String url = "http://" + first.getHost() + ":" + first.getPort() + "/product/" + productId;
        log.info("远程请求: {}", url);
        // 给远程发送请求
        return restTemplate.getForObject(url, Product.class);
    }
}
