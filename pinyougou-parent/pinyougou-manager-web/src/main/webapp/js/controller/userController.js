var app = new Vue({
    el: "#app",
    data: {
        sourceType:{1:'PC',2:'H5',3:'Android',4:'IOS',5:'WeChat'},
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        ids: [],
        searchEntity: {}
    },
    methods: {
        searchList: function (curPage) {
            axios.post('/user/search.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
                app.list = response.data.list;
                app.pageNo = curPage;
                app.pages = response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll: function () {
            console.log(app);
            axios.get('/user/findAll.shtml').then(function (response) {
                app.list = response.data;
            }).catch(function (error) {
                console.log(error);
            })
        },
        findPage: function () {
            var that = this;
            axios.get('/user/findPage.shtml', {
                params: {
                    pageNo: this.pageNo
                }
            }).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data.list;
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            }).catch(function (error) {
                console.log(error);
            })
        },
        //该方法只要不在生命周期的
        add: function () {
            axios.post('/user/add.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log(error);
            });
        },
        update: function () {
            axios.post('/user/update.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(app.pageNo);
                }
            }).catch(function (error) {
                console.log(error);
            });
        },
        save: function () {
            if (this.entity.id != null) {
                this.update();
            } else {
                this.add();
            }
        },
        findOne: function (id) {
            axios.get('/user/findOne/' + id + '.shtml').then(function (response) {
                app.entity = response.data;
            }).catch(function (error) {
                console.log(error);
            });
        },
        dele: function () {
            axios.post('/user/delete.shtml', this.ids).then(function (response) {
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log(error);
            });
        },
        format:function (dateNum) {
            if(dateNum==undefined||dateNum==""){
                return ""
            }
            return new Date(dateNum).toLocaleDateString();
        }

    },
    //钩子函数 初始化了事件和
    created: function () {

        this.searchList(1);

    }

})
