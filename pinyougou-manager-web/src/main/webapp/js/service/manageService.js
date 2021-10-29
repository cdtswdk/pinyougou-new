/***
 * 创建一个服务层
 * 抽取发送请求的一部分代码
 * */
app.service("manageService", function ($http) {

    //查询列表
    this.findAll = function (page, size, searchEntity) {
        return $http.post("/management/list.shtml?page=" + page + "&size=" + size, searchEntity);
    }

    //增加Manager
    this.add = function (entity) {
        return $http.post("/management/add.shtml", entity);
    }

    //保存
    this.update = function (entity) {
        return $http.post("/management/update.shtml", entity);
    }

    //根据ID查询
    this.findOne = function (id) {
        return $http.get("/management/" + id + ".shtml");
    }

    //批量删除
    this.delete = function (ids) {
        return $http.post("/management/delete.shtml", ids);
    }

    // 更新密码
    this.updatePassword = function (prePwd, newPwd, cfmPwd) {
        return $http.post('/management/updatePwd.shtml?prePwd=' + prePwd + '&newPwd=' + newPwd + '&cfmPwd=' + cfmPwd);
    }

});
