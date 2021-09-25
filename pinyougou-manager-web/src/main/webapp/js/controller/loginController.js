/*
* 创建一个控制层
* */
app.controller('loginController', function ($scope, loginService) {

    // 实现登录
    $scope.login = function () {
        $scope.msg = "正在登录....";
        loginService.login($scope.username, $scope.password).success(function (response) {
            if (response.success) {
                location.href = response.message;
            } else {
                $scope.msg = response.message;
            }
        });
    }

    // 调用查询用户名的方法
    $scope.getLoginName = function () {
        loginService.loginName().success(function (response) {
            $scope.username = response;
        });
    }

    // 最后登录时间
    $scope.getLastLoginTime = function () {
        loginService.lastLoginTime().success(function (response) {
            $scope.lastLoginDate = response;
        });
    }
});