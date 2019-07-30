var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        goodsList:[],
        entity:{},
        orderList:[],
        orderEntity:{order:{},goods:{}},
        ids:[],
        searchEntity:{}
    },
    methods: {
        searchList:function (curPage) {
            axios.post('/seckillOrder/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
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
            axios.get('/seckillGoods/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },

        findGoods:function () {
            console.log(app);
            axios.get('/seckillGoods/selectdAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.goodsList=response.data;

            }).catch(function (error) {

            })
            this.pinjie()
        },

         findPage:function () {
            var that = this;
            axios.get('/seckillOrder/findPage.shtml',{params:{
                pageNo:this.pageNo
            }}).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                alert("yang")
                app.list=response.data.list;
                //总页数
                app.pages=response.data.pages;
                alert(1)
                //app.pageNo=curPage;
                alert(2)
            }).catch(function (error) {

            })
        },
        //该方法只要不在生命周期的
        add:function () {
            axios.post('/seckillOrder/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/seckillOrder/update.shtml',this.entity).then(function (response) {
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
            axios.get('/seckillOrder/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/seckillOrder/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        pinjie:function () {
            for(var i=0;i<this.list.length;i++){
                this.orderEntity.order=list.get(i)
                console(1);
                alert(1)
                this.orderList.push(this.orderEntity)
            }
            for(var i=0;i<this.orderList.length;i++){
                for(var j=0;j<this.goodsList.length;j++){
                    if(this.orderList.get(i).seckillId==this.goodsList.get(j).id){
                        this.orderList.get(i).goods=this.goodsList.get(j);
                    }
                }
            }
        },

        orderDetail:function (id) {
            window.location.href="seckill_orderDetail.html?id="+id;
        }

    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.searchList(1);
        //this.findPage()
        //this.findGoods();
        //this.pinjie()

    }

})
