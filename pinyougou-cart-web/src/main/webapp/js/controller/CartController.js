app.controller('cartController',function ($scope,cartService) {
    //查询购物车数据
    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList = response;

            //计算总数量和总价格
            sum($scope.cartList);
        })
    }

    //计算总数量和总价格
    sum=function(cartList){
        $scope.totalValue={totalNum:0,totalMoney:0.0};
        for (let i=0;i<cartList.length;i++){
            let itemList = cartList[i].orderItemList;
            for (let j=0;j<itemList.length;j++){
                $scope.totalValue.totalNum +=itemList[j].num;
                $scope.totalValue.totalMoney +=itemList[j].totalFee;
            }
        }
    }
    //更改购物车商品数量
    $scope.addGoodsToCartList = function (itemId,num) {
        cartService.addGoodsToCartList(itemId,num).success(function (response) {
            if(response.success){
                $scope.findCartList();
            }else {
                alert(response.message);
            }
        })
    }
})