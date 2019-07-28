var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        searchEntity:{},
        startTime:"",
        endTime:""
    },
    methods: {
       showAll:function () {
           axios.get('/order/findAllSales.shtml').then(function (response) {
              app.searchEntity=response.data;//List<TbOrderItem>
           })
       },
        showOneTime:function () {
            axios.get('/order/findOneTimeSales.shtml',{
                params:{
                    startTime:app.startTime,
                    endTime:app.endTime
                }
            }).then(function (response) {
                app.searchEntity=response.data;//List<TbOrderItem>
            })
        }




    },
    //钩子函数 初始化了事件和
    created: function () {
      this.showAll();

    }

});
