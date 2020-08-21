app.service('loginService', function ($http) {
    //获取用户登录名
    this.showName = function () {
        return $http.get('user/login/name.shtml');
    }
})