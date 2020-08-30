app.controller('payController', function ($scope, $location, payService) {

    $scope.getMoney = function () {
        return $location.search()['money'];
    }

    //创建二维码
    $scope.createNative = function () {
        payService.createNative().success(function (response) {
            $scope.out_trade_no = response.out_trade_no;
            $scope.total_fee = (response.total_fee / 100).toFixed(2);
            $scope.code_url = response.code_url;

            //生成二维码
            let qr = new QRious({
                element: document.getElementById('payImg'),
                size: 250,
                value: $scope.code_url,
                level: 'H'
            })
            payService.queryPayStatus($scope.out_trade_no).success(function (response) {
                if (response.success) {
                    location.href = "/paysuccess.html?money=" + $scope.total_fee;
                } else {
                    //$scope.createNative();
                    location.href = "/payfail.html";
                }
            })
        })
    }
})