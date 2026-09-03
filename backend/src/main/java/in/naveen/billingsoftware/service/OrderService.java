package in.naveen.billingsoftware.service;

import java.time.LocalDate;

import java.util.List;

import in.naveen.billingsoftware.io.OrderRequest;
import in.naveen.billingsoftware.io.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request);

    void deleteOrder(String orderId);

    List<OrderResponse> getLatestOrders();

       Double sumSalesByDate(LocalDate date);

    Long countByOrderDate(LocalDate date);

    List<OrderResponse> findRecentOrders();
}
