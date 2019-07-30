var myChart1 = echarts.init(document.getElementById('chart1'));
var app = new Vue({
    el: "#app",
    data: {
        list: [],
        entity: {}
    },
    methods: {
        showChart: function () {
            axios.get("/user/showChart.shtml").then((res) => {
                myChart1.setOption(
                    {
                        title: {
                            text: '用户活跃度分析'
                        },
                        tooltip: {},
                        legend: {
                            data: ['活跃人数']
                        },
                        xAxis: {
                            data: res.data.time
                        },
                        yAxis: {
                            type: 'value'
                        },
                        series: [{
                            name: '活跃人数',
                            type: 'line',
                            data: res.data.count
                        }]
                    });
            });
        }
    },
    created: function () {
        this.showChart();
    }
})
