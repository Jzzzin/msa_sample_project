package com.bloknoma.ftgo.restaurantservice.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.bloknoma.ftgo.restaurantservice.aws.ApiGatewayResponse;
import com.bloknoma.ftgo.restaurantservice.domain.Restaurant;
import com.bloknoma.ftgo.restaurantservice.domain.RestaurantService;
import com.bloknoma.ftgo.restaurantservice.events.CreateRestaurantRequest;
import com.bloknoma.ftgo.restaurantservice.web.CreateRestaurantResponse;
import io.eventuate.common.json.mapper.JSonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.bloknoma.ftgo.restaurantservice.aws.ApiGatewayResponse.applicationJsonHeaders;

// create restaurant
@Configuration
@Import(RestaurantServiceLambdaConfiguration.class)
public class CreateRestaurantRequestHandler extends AbstractAutowiringHttpRequestHandler {

    @Autowired
    private RestaurantService restaurantService;

    // app ctx 구성 클래스
    @Override
    protected Class<?> getApplicationContextClass() {
        return CreateRestaurantRequestHandler.class;
    }

    @Override
    protected APIGatewayProxyResponseEvent handleHttpRequest(APIGatewayProxyRequestEvent request, Context context) {

        CreateRestaurantRequest crr = JSonMapper.fromJson(request.getBody(), CreateRestaurantRequest.class);

        Restaurant rest = restaurantService.create(crr);

        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(new CreateRestaurantResponse(rest.getId()))
                .setHeaders(applicationJsonHeaders())
                .build();
    }
}
