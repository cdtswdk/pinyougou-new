/*****
 * 定义一个控制层 controller
 * 发送HTTP请求从后台获取数据
 ****/
app.controller("contentController", function ($scope, $http, contentService) {

    $scope.contentList = {};

    //根据categoryId查询广告
    $scope.getByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(function (response) {
            //存储所有广告
            $scope.contentList[categoryId] = response;
        })
    }

    //搜索跳转搜索页
    $scope.search = function () {
        location.href = "http://localhost:18087?keyword=" + $scope.keyword;
    }

});
