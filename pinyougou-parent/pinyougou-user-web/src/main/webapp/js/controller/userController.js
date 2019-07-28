Vue.use(VeeValidate,{locale:'zh_CN'})
var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        ids: [],
        searchEntity: {},
        SmsCode:'',
        name:'',
        addresses:[]
    },
    methods: {
        searchList: function (curPage) {
            axios.post('/user/search.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
                //获取数据
                app.list = response.data.list;

                //当前页
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll: function () {
            console.log(app);
            axios.get('/user/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data;

            }).catch(function (error) {

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
                console.log("1231312131321");
            });
        },
        update: function () {
            axios.post('/user/update.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
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
                console.log("1231312131321");
            });
        },
        dele: function () {
            axios.post('/user/delete.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        formSubmit:function(){
            this.$validator.validate().then(result=>{
                if(!result){
                    alert("格式不正确");
                } else{
                    this.register();
                }
            })
        },



        register: function () {
            axios.post("user/add/"+app.SmsCode+".shtml",app.entity).then(function (response) {
                if(response.data.success) {
                    window.location.href = "home-index.html"
                } else{
                    app.$validator.errors.add(response.data.errorsList);
                }
            }).catch(function (error) {
                console.log("343242")
            })
        },

        getCode:function(phone){
            axios.get("user/getCode/"+phone+".shtml").then(function (response) {

                    alert(response.data.message);
            })
        },
        addUserInfo:function () {
            axios.post("/user/addUserInfo.shtml",app.userInfo).then(function (response) {
                if (response.data) {
                    alert(response.data.message)
                }else {
                    alert(response.data.message)
                }
            })
        },
        //地址管理查询
        findAddress:function () {
            axios.get("/user/findAddress.shtml").then(function (response) {
                 app.addresses=response.data;
            })
        }


    },
    //钩子函数 初始化了事件和
    created: function () {

        this.getName();


    }

})
