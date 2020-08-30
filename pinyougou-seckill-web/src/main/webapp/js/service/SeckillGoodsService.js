app.service('SeckillService', function ($http) {
    //查询秒杀商品列表
    this.list = function () {
        return $http.get('/seckill/goods/list.shtml');
    }
    //根据id获取商品
    this.getOne = function (id) {
        return $http.get('/seckill/goods/one.shtml?id=' + id);
    }
    //用户下单
    this.add = function (id) {
        return $http.get('/seckill/order/add.shtml?id=' + id);
    }
})