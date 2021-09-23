/*
* 创建一个控制层
* */
app.controller('loginController', function ($scope, loginService) {

    // 实现登录
    $scope.login = function () {
        loginService.login($scope.username,$scope.password).success(function (response) {

        })
    }

    // 调用查询用户名的方法
    $scope.getLoginName = function () {
        loginService.loginName().success(function (response) {
            $scope.username = response;
        });
    }
});