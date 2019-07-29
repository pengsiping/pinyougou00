var app = new Vue({
    el:"#app",
    data:{
        payObject:{}//封装支付的金额 二维码连接 交易订单号
    },
    methods:{
        createNative:function (orderId,totalFee) {
            var that=this;
            axios.get('/pay/createNative.shtml?orderId='+orderId+'&totalFee='+totalFee).then(
                function (response) {
                    //如果有数据
                    if(response.data){
                        app.payObject=response.data;
                        //app.payObject.total_fee=app.payObject.total_fee/100;
                        //生成二维码
                        var qr = new QRious({
                            element:document.getElementById('qrious'),
                            size:250,
                            level:'H',
                            value:app.payObject.code_url
                        });
                        //alert(app.payObject.code_url)
                    }
                }
            )
        }

    },
    //钩子函数
    created:function () {
        var urlParam=this.getUrlParam();
        this.payObject.out_trade_no=urlParam.orderId;
        this.payObject.total_fee=urlParam.totalFee;
        //app.payObject.total_fee=this.getUrlParam("totalFee");
        //页面一加载就应当调用
        this.createNative(this.payObject.out_trade_no,this.payObject.total_fee);
    }

})
