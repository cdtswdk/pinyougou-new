app.service('cartService', function ($http) {
    this.findCartList = function () {
        return $http.get('/cart/list.shtml');
    }
    this.addGoodsToCartList = function (itemId, num) {
        return $http.get('/cart/add.shtml?itemId=' + itemId + '&num=' + num);
    }
    this.findAddressList=function () {
        return $http.get('/address/user/list.shtml');
    }
    this.submitOrder=function (order) {
        return $http.post('order/add.shtml',order);
    }
})