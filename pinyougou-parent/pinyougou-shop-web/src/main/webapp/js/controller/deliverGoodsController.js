var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        ids: [],
        searchEntity: {},
        status: ['未付款', '已付款', '未发货', '已发货']
    },
    methods: {
        searchList: function (curPage) {
            axios.post('/deliver/search.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
                //获取数据
                app.list = response.data.list;

                //当前页
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            });
        },
        update: function (status) {
            axios.post('/deliver/update/'+status+'.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        }

        },
        //钩子函数 初始化了事件和
        created: function () {

            this.searchList(1);

        }


})
