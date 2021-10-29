/*****
 * 定义一个控制层 controller
 * 发送HTTP请求从后台获取数据
 ****/
app.controller("manageController", function ($scope, $http, $controller, manageService) {

    //继承父控制器
    $controller("baseController", {$scope: $scope});


    //获取所有的Manager信息
    $scope.getPage = function (page, size) {
        //发送请求获取数据
        manageService.findAll(page, size, $scope.searchEntity).success(function (response) {
            //集合数据
            $scope.list = response.list;
            //分页数据
            $scope.paginationConf.totalItems = response.total;
        });
        alert($scope.list);
    }

    //添加或者修改方法
    $scope.save = function () {
        //增加操作
        let result = manageService.add($scope.entity);
        //判断操作流程
        result.success(function (response) {
            //判断执行状态
            if (response.success) {
                //注册成功后跳转到登录页
                location.href = "/login.html";
            } else {
                //打印错误消息
                alert(response.message);
            }
        });
    }

    // 更新密码
    $scope.updatePwd = function (prePwd, newPwd, cfmPwd) {
        manageService.updatePassword(prePwd, newPwd, cfmPwd).success(function (response) {
            if (response.success) {
                alert(response.message);
                location.href = '/admin/index.html'
            } else {
                alert(response.message);
                location.reload();
            }
        })
    }

    //根据ID查询信息
    $scope.getById = function (id) {
        sellerService.findOne(id).success(function (response) {
            //将后台的数据绑定到前台
            $scope.entity = response;
        });
    }

    //批量删除
    $scope.delete = function () {
        sellerService.delete($scope.selectids).success(function (response) {
            //判断删除状态
            if (response.success) {
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        });
    }
});
