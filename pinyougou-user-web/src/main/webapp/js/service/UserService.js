app.service('userService',function ($http) {
    //发送验证码
    this.sendCode=function (phone) {
        return $http.get('user/sendCode.shtml?phone='+phone);
    }

    this.reg=function (entity,code) {
        return $http.post('/user/add.shtml?code='+code,entity);
    }
})