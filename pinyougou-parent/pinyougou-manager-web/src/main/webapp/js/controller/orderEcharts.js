var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        searchEntity:{},
        goods:{value:0,name:''},
        dataValue:[],
        startTime:'',
        endTime:''
    },
    methods: {
        searchList:function (curPage) {

            axios.post('/order/findOrder.shtml').then(function (response) {
                //获取数据
                app.list=response.data.list;
                for(var i=0;i<response.data.list.length;i++){
                    app.goods.value=response.data.list[i].num
                    app.goods.name=response.data.list[i].goodsName
                    var good=JSON.stringify(app.goods)
                    app.dataValue.push(JSON.parse(good))
                }


                //myChart1.setOption(option);
                //当前页
                app.pageNo=curPage;
                //总页数
                    app.showGoods()

                app.pages=response.data.pages;
            }

            );

        },


        showGoods:function(){

            var myChart = echarts.init(document.getElementById('pie_echarts'));
            option={
                title: {
                    text: '销量统计',
                    x: 'left'
                },
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b} : {c} ({d}%)"
                },
                color: ['#CD5C5C', '#00CED1', '#9ACD32', '#FFC0CB'],
                stillShowZeroSum: false,
                series: [
                    {
                        name: '销量',
                        type: 'pie',
                        radius: '80%',
                        center: ['60%', '60%'],
                        data: app.dataValue
                        ,
                        itemStyle: {
                            emphasis: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(128, 128, 128, 0.5)'
                            }
                        }
                    }
                ]
            };
            myChart.setOption(option);
        },

        searchByTime:function () {
            axios.post('/order/findOrderTotal.shtml',{
                params:{
                    startTime:app.startTime,
                    endTime:app.endTime
                }}).then(function (response) {
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
            axios.get('/order/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
        findPage:function () {
            var that = this;
            axios.get('/order/findPage.shtml',{params:{
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


        findOne:function (id) {
            axios.get('/order/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        orderDetail:function (id) {
            window.location.href="orderDetail.html?id="+id;
        }



    },
    //钩子函数 初始化了事件和
    created: function () {

        this.searchList(1);

    }

})
