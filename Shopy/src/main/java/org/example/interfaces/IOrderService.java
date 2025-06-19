package org.example.interfaces;

import org.example.model.Order;
import org.example.model.OrderResult;

public interface IOrderService {
    OrderResult processOrder(Order order);
}
