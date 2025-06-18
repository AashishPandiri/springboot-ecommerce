package com.backend.ecommerce.service;

import com.backend.ecommerce.model.LocalUser;
import com.backend.ecommerce.model.WebOrder;
import com.backend.ecommerce.model.dao.WebOrderDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private WebOrderDAO webOrderDAO;

    public OrderService(WebOrderDAO webOrderDAO){
        this.webOrderDAO = webOrderDAO;
    }

    public List<WebOrder> getOrders(LocalUser user){
        return webOrderDAO.findByUser(user);
    }
}
