app.controller("searchController", function ($scope, $http, $location, searchService) {

    //搜索兑现
    $scope.searchMap = {
        "keyword": "",
        "category": "",
        "brand": "",
        "spec": {},
        "price": "",
        "pageNum": 1,
        "size": 10,
        "sortType": "",
        "sortField": ""
    };

    $scope.loadKeyword = function () {
        let keyword = $location.search()['keyword'];
        if (keyword != null) {
            $scope.searchMap.keyword = keyword;
        }
    }


    //定义一个品牌集合对象
    $scope.resultMap = {brandList: []};

    //品牌关键字搜索
    $scope.loadKeywordBrand = function () {
        if ($scope.resultMap.brandList != null) {
            for (let i = 0; i < $scope.resultMap.brandList.length; i++) {
                //获取品牌名字
                let brandName = $scope.resultMap.brandList[i].text;
                let index = $scope.searchMap.keyword.indexOf(brandName);
                if (index >= 0) {
                    return true;
                }
            }
        }
        return false;
    }


    //排序搜索
    $scope.sortSearch = function (sortType, sortField) {
        $scope.searchMap.sortType = sortType;
        $scope.searchMap.sortField = sortField;
        //执行搜索
        $scope.search();
    }

    //移除筛选条件
    $scope.removeSearchItem = function (key) {
        if (key == "category" || key == "brand" || key == "price") {
            $scope.searchMap[key] = "";
        } else {
            //规格数据
            delete $scope.searchMap.spec[key];
        }
        //搜索实现
        $scope.search();
    }

    //添加搜索条件
    $scope.addSearchItem = function (key, value) {
        if (key == "category" || key == "brand" || key == "price") {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        //搜索实现
        $scope.search();
    }

    //搜索方法
    $scope.search = function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;
            $scope.pageHandler(response.total, $scope.searchMap.pageNum);
        });
    }

    //分页定义
    $scope.page = {
        size: 10,        //每页显示多少条
        total: 0,        //总共有多少条记录
        pageNum: 1,      //当前页
        offset: 1,       //偏移量
        lpage: 1,        //起始页
        rpage: 5,        //结束页
        totalPage: 1,    //总页数
        pages: [],       //页码
        nextPage: 1,     //下一页
        prePage: 1,       //上一页
        hasPre: 0,       //是否有上页
        hasNext: 0       //是否有下页
    }
    /*
    * 分页计算
    * total:总记录数
    * pageNum：当前页
    * */
    $scope.pageHandler = function (total, pageNum) {
        //总记录数
        $scope.page.total = total;
        //当前页
        $scope.page.pageNum = pageNum;
        //总页数
        let totalPage = total % $scope.page.size === 0 ? total / $scope.page.size : parseInt((total / $scope.page.size) + 1);
        $scope.page.totalPage = totalPage;

        //偏移量
        let offset = $scope.page.offset;
        //起始页
        let lpage = $scope.page.lpage;
        //结束页
        let rpage = $scope.page.rpage;

        //分页操作
        if (pageNum - offset > 0) {
            lpage = pageNum - offset;
            rpage = pageNum + offset;
        }
        if (pageNum - offset <= 0) {
            lpage = 1;
            rpage = pageNum + offset + Math.abs(pageNum - offset) + 1;
        }
        if (rpage > totalPage) {
            lpage = lpage - (rpage - totalPage);
            rpage = totalPage;
        }
        if (lpage <= 0) {
            lpage = 1;
        }

        //添加分页集合
        //清空原有的集合
        $scope.page.pages = [];
        for (let i = lpage; i <= rpage; i++) {
            $scope.page.pages.push(i);
        }

        //数据源存入$scope.page中
        $scope.page.lpage = lpage;
        $scope.page.rpage = rpage;

        //上一页
        $scope.page.prePage = (pageNum - 1) > 0 ? pageNum - 1 : 1;
        //下一页
        $scope.page.nextPage = (pageNum + 1) > totalPage ? totalPage : pageNum + 1;
        //是否有上一页
        $scope.page.hasPre = (pageNum - 1) > 0 ? 1 : 0;
        //是否有下一页
        $scope.page.hasNext = (pageNum + 1) > totalPage ? 0 : 1;
    }

    //分页搜索
    $scope.pageSearch = function (pageNum) {
        //每页显示N条
        $scope.searchMap.size = $scope.page.size;
        if (!isNaN(pageNum) && pageNum > 0) {
            //将字符串数据转成int类型
            $scope.searchMap.pageNum = parseInt(pageNum);
            //防止输入数字超过了总页数
            if ($scope.searchMap.pageNum > $scope.page.totalPage) {
                $scope.searchMap.pageNum = $scope.page.totalPage;
            }
        }
        if (isNaN(pageNum)) {
            $scope.searchMap.pageNum = 1;
        }
        //执行搜索
        $scope.search();
    }
})