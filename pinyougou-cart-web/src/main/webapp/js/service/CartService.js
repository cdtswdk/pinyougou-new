app.service('cartService', function ($http) {
    this.findCartList = function () {
        return $http.get('/cart/list.shtml');
    }
    this.addGoodsToCartList = function (itemId, num) {
        return $http.get('/cart/add.shtml?itemId=' + itemId + '&num=' + num);
    }
})