/*
* 编写一个查询用户的Service
* */
app.service('loginService', function ($http) {

    // 登录
    this.login = function (username, password) {
        return $http.post('/login?username=' + username + '&password=' + password);
    }

    //获取用户登录名
    this.loginName = function () {
        return $http.get('/login/name.shtml');
    }

})