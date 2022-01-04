package com.estore.user.query.rest;
 
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.estore.core.model.User;
import com.estore.core.query.FetchUserPaymentDetailQuery;

@RestController
@RequestMapping("/users")
public class UsersQueryController {

    @Autowired
    QueryGateway queryGateway;

    @GetMapping("/{userId}/payment-details")
    public User getUserPaymentDetails(@PathVariable String userId) {   
        FetchUserPaymentDetailQuery query = new FetchUserPaymentDetailQuery(userId);
        return queryGateway.query(query, ResponseTypes.instanceOf(User.class)).join();
    }
}