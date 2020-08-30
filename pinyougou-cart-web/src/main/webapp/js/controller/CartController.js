app.controller('cartController', function ($scope, cartService) {


    //提交订单
    $scope.submitOrder = function () {
        //收货人地址
        $scope.order.receiverAreaName = $scope.address.address;
        //收货人手机
        $scope.order.receiverMobile = $scope.address.mobile;
        //收货人姓名
        $scope.order.receiver = $scope.address.contact;

        cartService.submitOrder($scope.order).success(function (response) {
            if (response.success) {
                if ($scope.order.paymentType === '1') {//如果是微信支付，跳转到支付页面
                    location.href = "pay.html";
                } else {//如果货到付款，跳转到提示页面
                    location.href = "paysuccess.html";
                }
            } else {
                alert(response.message);
            }
        })
    }

    //支付方式 1.在线支付 2.货到付款
    $scope.order = {paymentType: '1'}
    $scope.selectPayType = function (type) {
        $scope.order.paymentType = type;
    }


    //点击选择地址
    $scope.selectAddress = function (address) {
        $scope.address = address;
    }

    //判断选中了哪个地址
    $scope.isSelectedAddress = function (address) {
        if (address === $scope.address) {
            return true;
        } else {
            return false;
        }
    }

    //查询地址列表
    $scope.findAddressList = function () {
        cartService.findAddressList().success(function (response) {
            $scope.addressList = response;
            for (let i = 0; i < $scope.addressList.length; i++) {
                if ($scope.addressList[i].isDefault == '1') {
                    $scope.address = angular.copy($scope.addressList[i]);
                    return;
                }
            }
        })
    }

    //查询购物车数据
    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList = response;

            //计算总数量和总价格
            sum($scope.cartList);
        })
    }

    //计算总数量和总价格
    sum = function (cartList) {
        $scope.totalValue = {totalNum: 0, totalMoney: 0.0};
        for (let i = 0; i < cartList.length; i++) {
            let itemList = cartList[i].orderItemList;
            for (let j = 0; j < itemList.length; j++) {
                $scope.totalValue.totalNum += itemList[j].num;
                $scope.totalValue.totalMoney += itemList[j].totalFee;
            }
        }
    }
    //更改购物车商品数量
    $scope.addGoodsToCartList = function (itemId, num) {
        cartService.addGoodsToCartList(itemId, num).success(function (response) {
            if (response.success) {
                $scope.findCartList();
            } else {
                alert(response.message);
            }
        })
    }
})