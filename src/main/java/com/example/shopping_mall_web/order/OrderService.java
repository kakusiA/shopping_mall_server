package com.example.shopping_mall_web.order;

import com.example.shopping_mall_web.product.Product;
import com.example.shopping_mall_web.product.ProductRepository;
import com.example.shopping_mall_web.user.User;
import com.example.shopping_mall_web.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public List<Order> getOrdersByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(orderRepository::findByUser).orElse(null);
    }

    public Order createOrder(OrderDto orderDto) {
        Optional<User> user = userRepository.findById(orderDto.getUserId());
        if (user.isPresent()) {
            Order order = new Order();
            order.setUser(user.get());
            order.setTotalPrice(orderDto.getTotalPrice());
            order.setOrderDate(orderDto.getOrderDate());

            List<OrderDetail> orderDetails = orderDto.getOrderDetails().stream().map(detailDto -> {
                OrderDetail detail = new OrderDetail();
                detail.setOrder(order);
                Optional<Product> product = productRepository.findById(detailDto.getProductId());
                product.ifPresent(detail::setProduct);
                detail.setQuantity(detailDto.getQuantity());
                detail.setPricePerItem(detailDto.getPricePerItem());
                return detail;
            }).collect(Collectors.toList());

            order.setOrderDetails(orderDetails);
            return orderRepository.save(order);
        }
        return null;
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }
}
