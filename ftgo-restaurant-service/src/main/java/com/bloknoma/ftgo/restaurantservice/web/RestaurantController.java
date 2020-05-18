package com.bloknoma.ftgo.restaurantservice.web;

import com.bloknoma.ftgo.restaurantservice.domain.Restaurant;
import com.bloknoma.ftgo.restaurantservice.domain.RestaurantService;
import com.bloknoma.ftgo.restaurantservice.events.CreateRestaurantRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// REST API
@RestController
@RequestMapping(path = "/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    // 레스토랑 추가
    @RequestMapping(method = RequestMethod.POST)
    public CreateRestaurantResponse create(@RequestBody CreateRestaurantRequest request) {
        Restaurant r = restaurantService.create(request);
        return new CreateRestaurantResponse(r.getId());
    }

    // 레스토랑 조회
    @RequestMapping(path = "/{restaurantId}", method = RequestMethod.GET)
    public ResponseEntity<GetRestaurantResponse> get(@PathVariable long restaurantId) {
        return restaurantService.findById(restaurantId)
                .map(r -> new ResponseEntity<>(makeGetRestaurantResponse(r), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private GetRestaurantResponse makeGetRestaurantResponse(Restaurant r) {
        return new GetRestaurantResponse(r.getId(), r.getName());
    }
}
