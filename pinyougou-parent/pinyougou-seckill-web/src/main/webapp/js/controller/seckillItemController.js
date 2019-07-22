var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        ids: [],
        searchEntity: {},
        timeString: '',
        seckillId: 0,
        messageInfo: '',
        goodsInfo: {},
        timer: ''
    },
    methods: {
        convertTimeString: function (alltime) {
            var allsecond = Math.floor(alltime / 1000);  //毫秒转秒
            var days = Math.floor(allsecond / (24 * 3600)); //天数
            /*var hours = Math.floor((allsecond-days*24*3600)/3600);*/
            var hours = Math.floor((allsecond - days * 60 * 60 * 24) / (60 * 60));//小数数
            var minutes = Math.floor((allsecond - days * 24 * 3600 - hours * 3600) / 60);
            var seconds = Math.floor(allsecond - days * 24 * 3600 - hours * 3600 - minutes * 60);
            if (days > 0) {
                days = days + "天"
            }
            if (hours < 10) {
                hours = "0" + hours
            }
            if (minutes < 10) {
                minutes = "0" + minutes;
            }
            if (seconds < 10) {
                seconds = "0" + seconds;
            }
            return days + hours + ":" + minutes + ":" + seconds;
        },

        //倒计时
        caculate: function (alltime) {
            let clock = window.setInterval(function () {
                alltime = alltime - 1000;
                app.timeString = app.convertTimeString(alltime);
                if (alltime < 0) {
                    window.clearInterval(clock);
                }
            }, 1000)
        },

        submitOrder: function () {
            axios.get("seckillOrder/submit/" + this.seckillId + ".shtml").then(function (response) {
                if (response.data.success) {
                    app.messageInfo = response.data.message;
                } else {
                    if (response.data.message == "403") {
                        //沒有登入
                        var url = window.location.href;
                        /*http://localhost:9111/149187842867954.html?id=5*/
                        window.location.href = "/page/login.shtml?url=" + url;
                    } else if(response.data.message.indexOf("商品已被搶光")!=-1){
                        app.messageInfo = response.data.message;
                        window.clearInterval(app.timer);
                    } else {
                        app.messageInfo = response.data.message;
                    }

                }
            })
        },

        getGoodsById: function (id) {
            axios.get("seckillGoods/getGoodsById.shtml?id=" + id).then(function (response) {
                alert("dsada")
                console.log(response.data);
                app.goodsInfo = response.data;
                app.caculate(response.data.time);
                console.log(app.goodsInfo);
            })
        },

        queryStatus: function () {

            window.clearInterval(this.timer);
            var count = 0;
            this.timer = window.setInterval(function () {
                count += 3;
                axios.get("seckillOrder/queryOrderStatus.shtml").then(function (response) {
                    if (response.data.success) {
                        //抢单成功,跳转至登录页面
                        // window.location.href=?
                        window.location.href = "pay/pay.html"
                    } else {
                        if (response.data.message == "403") {
                            //需要重新登录
                            window.location.href = "/page/login.shtml?url=" + url;
                        } else {
                            app.messageInfo = response.data.message + "...." + count;
                        }
                    }
                })

            }, 3000)
        }

    },
    //钩子函数 初始化了事件
    created: function () {
        let urlParam = this.getUrlParam("id");
        this.seckillId = urlParam.id;
        this.getGoodsById(this.seckillId);

    }

})