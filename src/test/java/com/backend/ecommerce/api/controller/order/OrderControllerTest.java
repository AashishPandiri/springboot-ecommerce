package com.backend.ecommerce.api.controller.order;

import com.backend.ecommerce.model.WebOrder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testUnauthenticatedOrderList() throws Exception{
        mvc.perform(get("/order")).andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @WithUserDetails("UserA")
    public void testUserAAuthenticatedUserOrderList() throws Exception{
        testAuthenticatedListBelongsToUser("UserA");
    }

    @Test
    @WithUserDetails("UserB")
    public void testUserBAuthenticatedUserOrderList() throws Exception{
        testAuthenticatedListBelongsToUser("UserB");
    }

    private void testAuthenticatedListBelongsToUser(String username) throws Exception{
        mvc.perform(get("/order")).andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    List<WebOrder> orders = new ObjectMapper().readValue(json, new TypeReference<List<WebOrder>>() {});
                    for (WebOrder order: orders){
                        Assertions.assertEquals(username, order.getUser().getUsername(), "Order List should only be orders belonging to the user");
                    }
                });
    }
}
