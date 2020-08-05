app.controller("searchController",function ($scope,$http,searchService) {

    //搜索兑现
    $scope.searchMap = {"keyword":"","category":"","brand":"","spec":{}};

    //移除筛选条件
    $scope.removeSearchItem=function(key){
        if(key == "category" || key == "brand"){
            $scope.searchMap[key]="";
        }else {
            //规格数据
            delete $scope.searchMap.spec[key];
        }
        //搜索实现
        $scope.search();
    }

    //添加搜索条件
    $scope.addSearchItem = function(key,value){
        if(key == "category" || key == "brand"){
            $scope.searchMap[key]=value;
        }else {
            $scope.searchMap.spec[key]=value;
        }
        //搜索实现
        $scope.search();
    }

    //搜索方法
    $scope.search = function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;
        });
    }
})