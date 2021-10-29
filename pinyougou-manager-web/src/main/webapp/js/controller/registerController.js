/*
* 创建一个控制层
* */
app.controller('registerController', function ($scope, registerService) {

    //添加或者修改方法
    $scope.save = function () {
        //增加操作
        let result = registerService.add($scope.entity);
        //判断操作流程
        result.success(function (response) {
            $scope.msg = "正在注册....";
            //判断执行状态
            if (response.success) {
                alert("注册成功");
                //注册成功后跳转到登录页
                location.href = "/login.html";
            } else {
                //打印错误消息
                $scope.msg = response.message;
            }
        });
    }
});