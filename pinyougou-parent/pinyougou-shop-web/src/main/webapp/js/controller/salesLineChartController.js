var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        searchEntity:{},
        startDate:"",
        endDate:""
    },
    methods: {
       show:function () {
           // 基于准备好的dom，初始化echarts实例
           var myChart1 = echarts.init(document.getElementById('chart1'));



           axios.get("/sale/getSalesLineChart.shtml",{
               params:{
                   startDate: app.startDate,
                   endDate:app.endDate
               }
           }).then((response) => {
               myChart1.setOption(
                   {
                       title: {
                           text: '每日销售折线图'
                       },
                       tooltip: {},
                       legend: {
                           data: ['销量']
                       },
                       xAxis: {
                           data: response.data.oneday
                       },
                       yAxis: {
                           type: 'value'
                       },
                       series: [{
                           name: '数量',
                           type: 'line',
                           data: response.data.money
                       }]
                   });
               // 使用刚指定的配置项和数据显示图表。
               myChart1.setOption(option);
           });
       }
       
       



    },
    //钩子函数 初始化了事件和
    created: function () {
      


    }

})
