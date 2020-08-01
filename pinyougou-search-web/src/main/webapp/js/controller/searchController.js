app.controller("searchController",function ($scope,$http,searchService) {
    //搜索方法
    $scope.entity = {};
    $scope.search = function () {
        searchService.search($scope.entity).success(function (response) {
            $scope.list = response.rows;
            console.log(response);
            console.log(response.rows);
        });
    }
})