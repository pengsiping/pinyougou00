var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        searchEntity:{},
        isOnSale:['已下架','已上架'],
        status:['未审核','已审核','审核未通过','已关闭'],
        itemCatList:[]
    },
    methods: {
        searchList:function (curPage) {
            axios.post('/goods/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {

               // alert("查询成功")
                //获取数据
                app.list=response.data.list;

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;

            });
        },

        //查询所有品牌列表
        findAllItemCategory:function () {
            console.log(app);
            axios.get('/itemCat/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                //app.list=response.data;
               var list = response.data;

                for (var i = 0; i < list.length ; i++) {
                    app.itemCatList[list[i].id]=list[i].name;
                }
                //手动渲染
                app.$mount("#app");
                //alert(app.itemCatList);
            }).catch(function (error) {
                console.log("1231312131321");
            })
        },

        //上架商品
        onSale:function(isOnSale){
            alert("上下架")
            axios.post("/goods/updateSaleStatus/"+isOnSale+".shtml",this.ids).then(function (response) {
                app.ids=[];
                if(response.data.success){
                    //上下架成功,更新页面
                    app.searchList(app.pageNo);
                } else{
                    alert(response.data.message);
                    app.searchList(app.pageNo);
                }

            })
        },





         findPage:function () {
            var that = this;
            axios.get('/goods/findPage.shtml',{params:{
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
            axios.post('/goods/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(app.pageNo);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/goods/update.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(app.pageNo);
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
            axios.get('/goods/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/goods/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(app.pageNo);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        }



    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.searchList(1);
        this.findAllItemCategory();
    }

})
