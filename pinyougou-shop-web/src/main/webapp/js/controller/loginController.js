/*
* 登录Controller
* */
app.controller('loginController', function ($scope, loginService) {


    //实现登录
    $scope.login = function () {
        $scope.msg = "正在登录....";
        //$scope.username,$scope.password
        loginService.login($scope.username, $scope.password).success(function (response) {
            //success   message
            if (response.success) {
                //登陆成功跳转到后台主页
                //location.href='/admin/index.html';
                location.href = response.message;
            } else {
                //登录失败
                //alert(response.message);
                $scope.msg = response.message;
            }
        });
    }

    // 查询登录用户名
    $scope.getLoginName = function () {
        loginService.loginName().success(function (response) {
            $scope.username = response;
        })
    }
    // 查询用户最近登录时间
    $scope.getLastLoginTime = function () {
        loginService.lastLoginTime().success(function (response) {
            $scope.lastLoginDate = response;
        })
    }
});