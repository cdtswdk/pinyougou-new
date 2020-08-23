//商品详细页（控制层）
app.controller('itemController', function ($scope,$http) {
    //定义规格选中对象
    $scope.specList = {};

    //加入购物车
    $scope.addToCart = function () {
        //SKU对应的ID
        let itemId = $scope.sku.id;
        let num = $scope.num;
        //alert(itemId);
        $http.get('http://localhost:18093/cart/add.shtml?itemId='+itemId+'&num='+num,{'withCredentials':true}).success(function (response) {
            if(response.success){
                //跳转到购物车页面
                location.href='http://localhost:18093/cart.html';
            }else {
                alert(response.message);
            }
        })
    }

    //更改购物车数量
    $scope.addNum = function (num) {
        $scope.num = parseInt($scope.num) + parseInt(num);
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    }

    //加载默认的sku
    $scope.sku = {};
    $scope.loadSku = function () {
        $scope.sku = angular.fromJson(angular.toJson(skuList[0]));
        $scope.specList = $scope.sku.spec;
    }

    //匹配两个对象
    matchObject = function (map1, map2) {
        for (let key in map1) {
            if (map1[key] != map2[key]) {
                return false;
            }
        }
        return true;
    }

    //记录当前点击规格时记录点击的信息
    $scope.selectSpecList = function (key, value) {
        $scope.specList[key] = value;
        //匹配规格组合是否存在
        for (let i = 0; i < skuList.length; i++) {
            let currentSpec = angular.fromJson(skuList[i].spec);
            if (matchObject($scope.specList, currentSpec)) {
                $scope.sku = angular.fromJson(angular.toJson(skuList[i]));
                return;
            }
        }
        //否则没有当前规格组合
        $scope.sku = {id: 0, title: '----该产品已下架----', price: 0};
    }
    //选中判断
    $scope.isSelected = function (key, value) {
        if ($scope.specList[key] == value) {
            return true;
        }
        return false;
    }
})