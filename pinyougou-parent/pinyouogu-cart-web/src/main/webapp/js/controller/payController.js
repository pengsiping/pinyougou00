var app = new Vue({
    el: "#app",
    data: {
        totalMoney:0,
        payObject:{}
    },
    methods: {
        createNative:function(){
            axios.get("pay/createNative.shtml").then(function (response) {
                if(response.data){
                    app.payObject=response.data;
                    app.payObject.total_fee=app.payObject.total_fee/100;
                    var qr= new QRious({
                        element:document.getElementById('qrious'),
                        size:250,
                        level:'H',
                        value:app.payObject.code_url
                    });
                    if(qr){
                        app.queryPayStatus(app.payObject.out_trade_no);
                    }
                }
            })
        },

        queryPayStatus:function(out_trade_no){
            axios.get("pay/queryPayStatus.shtml",{params:{
                    out_trade_no:out_trade_no
                }}).then(function (response) {
                    if(response.data){
                        if(response.data.success){
                            window.location.href="paysuccess.html?money="+app.payObject.total_fee;
                        }else{
                            if(response.data.message=="超时"){
                                window.location.href="payfail.html"
                            }else{
                                app.queryPayStatus(out_trade_no);
                            }

                        }
                    } else{
                        alert("支付异常");
                    }

            })
        }

    },
    //钩子函数 初始化了事件
    created: function () {
        var href = window.location.href;
        if(href.indexOf("pay.html")!=-1){
            this.createNative();
        }else{
           var param= this.getUrlParam();
           if(param.money){
               this.totalMoney = param.money;
           }

        }

    }

})