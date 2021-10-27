/*
* 编写一个查询用户的Service
* */
app.service('registerService', function ($http) {

    //增加Manager
    this.add = function (entity) {
        return $http.post("/manager/add.shtml", entity);
    }

})