const fetch = require("node-fetch");
const DataLoader = require('dataloader');

class RestaurantServiceProxy {
    constructor(options) {
        this.restaurantServiceUrl = `${options.baseUrl}/restaurants`;
        // DataLoader 생성
        this.dataLoader = new DataLoader(restaurantIds => this.batchFindRestaurants(restaurantIds));
        console.log("this.restaurantServiceUrl", this.restaurantServiceUrl);
    }

    // DataLoader 로 로딩
    findRestaurant(restaurantId) {
        return this.dataLoader.load(restaurantId);
    }

    findRestaurantInternal(restaurantId) {
        return fetch(`${this.restaurantServiceUrl}/${restaurantId}`)
            .then(response => {
                console.log("response=", response.status);
                if (response.status === 200) {
                    return response.json().then(body => {
                        console.log("response=", body);
                        return Object.assign({id: restaurantId, name: body.name.name}, body);
                    })
                } else
                    return Promise.reject(new Error("cannot found restaurant for id" + restaurantId))
            });
    }

    // 배치 로딩
    batchFindRestaurants(restaurantIds) {
        console.log("restaurantIds=", restaurantIds);
        return Promise.all(restaurantIds.map(k => this.findRestaurantInternal(k)));
    }

}

module.exports = {RestaurantServiceProxy};