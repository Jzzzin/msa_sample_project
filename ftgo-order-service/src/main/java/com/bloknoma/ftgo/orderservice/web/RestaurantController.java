package com.bloknoma.ftgo.orderservice.web;

import com.bloknoma.ftgo.orderservice.domain.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

// 레스토랑 rest 컨트롤러
@RestController
@RequestMapping(path = "/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    // 레스토랑 찾기
    @RequestMapping(path = "/{restaurantId}", method = RequestMethod.GET)
    public ResponseEntity<GetRestaurantResponse> getRestaurant(@PathVariable long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .map(restaurant -> new ResponseEntity<>(new GetRestaurantResponse(restaurantId), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
