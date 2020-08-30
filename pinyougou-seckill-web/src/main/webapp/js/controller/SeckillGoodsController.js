app.controller('SeckillController', function ($scope, $interval, $location, SeckillService) {

    //用户下单
    $scope.add = function () {
        let id = $location.search()['id'];
        if (!isNaN(id)) {
            SeckillService.add(id).success(function (response) {
                if (response.success) {
                    location.href = "/pay.html";
                } else {
                    if (response.message === '403') {
                        alert('请登录');
                        location.href = "/login/loading.shtml";
                    } else {
                        alert(response.message);
                    }
                }
            })
        }
    }

    //查询秒杀商品列表
    $scope.list = function () {
        SeckillService.list().success(function (response) {
            $scope.list = response;
        })
    }
    //根据id查询秒杀商品
    $scope.getOne = function () {
        let id = $location.search()['id'];
        if (!isNaN(id)) {
            SeckillService.getOne(id).success(function (response) {
                $scope.item = response;
                //活动剩余时间等于结束时间-当前时间
                let num = new Date($scope.item.endTime).getTime() - new Date().getTime();
                let time = $interval(function () {
                    //单位为毫秒
                    num -= 1000;
                    if (num <= 0) {
                        $interval.cancel(time);
                        $scope.timeStr = '已结束';
                    } else {
                        $scope.timeStr = dateInfo(num);
                    }
                }, 1000)
            })
        }
    }

    //将毫秒转换成天时分秒
    dateInfo = function (num) {
        var oneSecond = 1000;
        var oneMinute = oneSecond * 60;
        var oneHour = oneMinute * 60
        var oneDay = oneHour * 24;

        //天数
        var days = Math.floor(num / oneDay);
        //小时
        var hours = Math.floor((num % oneDay) / oneHour);
        //分钟
        var minutes = parseInt((num % oneHour) / oneMinute);
        //秒
        var seconds = parseInt((num % oneMinute) / oneSecond);

        //拼接时间格式
        var str = days + '天' + hours + '时' + minutes + '分' + seconds + '秒';
        return str;
    }
})