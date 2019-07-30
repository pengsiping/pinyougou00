var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        searchEntity:{},
        status:['待审核','已通过','未通过'],
        firstCharList:[],
        brandList:[],
        firstChar:''
    },
    methods: {
        updateStatus:function (status) {
            axios.post('/brandApplication/updateStatus/'+status+'.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.ids=[];
                    app.searchList(app.pageNo);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        findFirstChar:function () {
          axios.get('/brandApplication/findFirstChar.shtml').then(function (response) {
              app.firstCharList=response.data;
          });
        },

        searchList:function (curPage) {
            axios.post('/brandApplication/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据
                app.list=response.data.list;

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll:function () {
            console.log(app);
            axios.get('/brandApplication/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/brandApplication/findPage.shtml',{params:{
                pageNo:this.pageNo
            }}).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data.list;
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            }).catch(function (error) {

            })
        },
        //该方法只要不在生命周期的
        add:function () {
            axios.post('/brandApplication/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/brandApplication/update.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save:function () {
            if(this.entity.id!=null){
                this.update();
            }else{
                this.add();
            }
        },
        findOne:function (id) {
            axios.get('/brandApplication/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/brandApplication/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        }



    },
    watch:{
        'entity.firstChar':function (newval,oldval) {
            if(newval != undefined) {
                axios.post('/brandApplication/findBrandByFirstChar.shtml?firstChar='+newval).then(function (response) {
                    app.brandList=response.data;
                })
            }

        }
    },
    //钩子函数 初始化了事件和
    created: function () {
        this.searchList(1);

    }

})
