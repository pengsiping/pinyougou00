var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        ids: [],
        searchEntity: {},
        resultMap: {},  //封装结果类型
        searchMap: {'keyword': '', 'category': '', 'brand': '', spec: {}, 'price': '', 'pageNo': 1, 'pageSize': 40,'sortFile':'','sortType':''}, //封装搜索条件
        pageLabels: [] ,//页码存储变量
        preDott:false,
        nextDott:false

    },
    methods: {
        searchList: function () {
            axios.post("/itemSearch/search.shtml", this.searchMap).then(function (response) {
                app.resultMap = response.data;
                app.buildPageLabel();
            })
        },
        addSearchItem: function (key, value) {

            if (key == 'category' || key == 'brand' || key == 'price') {

                this.searchMap[key] = value;

            } else {

                this.searchMap.spec[key] = value;
            }
            this.searchList();
        },
        removeSearchItem: function (key) {
            if (key == 'category' || key == 'brand' || key == 'price') {
                this.searchMap[key] = "";
            } else {
                delete this.searchMap.spec[key];
            }

            this.searchList();
        },

        //分页
        buildPageLabel: function () {
            //设置页码只显示5条
            var totalPages =this.resultMap.totalPages;
            var firstPage = 1;
            var lastPage = totalPages;
            if (totalPages > 5) {

                if (this.searchMap.pageNo <= 3) {

                    var firstPage = 1;
                    var lastPage = 5;
                    this.preDott=false;
                    this.nextDott=true;

                } else if (this.searchMap.pageNo > totalPages - 2) {
                    //totalPages==7
                    firstPage = totalPages - 4;
                    lastPage = totalPages;
                    this.preDott=true;
                    this.nextDott=false;

                } else {
                    firstPage = this.searchMap.pageNo - 2;
                    lastPage = this.searchMap.pageNo + 2;
                    this.preDott=true;
                    this.nextDott=true;
                }
            } else {
                this.preDott=false;
                this.nextDott=false;
            }
            this.pageLabels=[];

            for (var i = firstPage; i <= lastPage ; i++) {
                this.pageLabels.push(i);
            }

        },


        queryPage:function(pageNo){
            pageNo= parseInt(pageNo);
            if(pageNo<=1){
                pageNo=1;
            }
            if(pageNo>this.resultMap.totalPages){
                pageNo=this.resultMap.totalPages;
            }
            this.searchMap.pageNo=pageNo;
            this.searchList();
        },
        clear:function(){
            this.searchMap= {'keyword': this.searchMap.keyword, 'category': '', 'brand': '', spec: {}, 'price': '', 'pageNo': 1, 'pageSize': 40,'sortFile':'','sortType':''}
        },

        //价格排序
        doSort:function(sortType,sortField){
            this.searchMap.sortType=sortType;
            this.searchMap.sortField=sortField;
            this.searchList();
        },

        //判断搜索内容是否是品牌关键字
        isKeywordsIsBrand:function(){
            if(this.resultMap.brandList!=null&&this.resultMap.brandList.length>0){
                for (var i = 0; i < this.resultMap.brandList.length; i++) {
                    if(this.searchMap.keyword.indexOf(this.resultMap.brandList[i].text)!=-1){
                        this.searchMap.brand=this.resultMap.brandList[i].text;
                        return true;
                    }
                }
            }
            return false;
        }



    },
    //钩子函数 初始化了事件和
    created: function () {

        var urlParam = this.getUrlParam(); // {"keyword":"三星"}
        if(urlParam!=null&&urlParam.keyword!=undefined){

            this.searchMap.keyword=decodeURIComponent(urlParam.keyword);
            this.searchList();
        }


    }

})
