var app = new Vue({
    el: "#app",
    data: {
        unPayOrderList:[],
        //item:[],//{'item':orderItem,'spec':''}
        //orderItem:{'itemId':0}
        orderList:[],
        order:{orderId:''},
        totalNum:0,
        totalMoney:0,
        myOrders:[],
        name:'',
        orderItem:{}
        },
    methods:{
        findUnPayOrders:function () {
            axios.get('/user/findUnpayOrders.shtml').then(function (response) {
                if (response.data!=null){
                    app.unPayOrderList= response.data;
                }
            })
        },

        findMyOrders:function () {
            axios.get('/user/findMyOrders.shtml').then(function (response) {
                app.getName();

                if (response.data!=null){
                    app.orderList= response.data;
                }
            })
        },
        getName:function(){
            alert("get");
            axios.get("login/getName.shtml").then(function (response) {
                app.name=response.data;
            })
        },
        submitOrder:function (orderId, totalFee) {
            alert(totalFee);
            window.location.href="pay.html?orderId="+orderId+"&totalFee="+totalFee;

        },

    },

    created:function () {
        this.findUnPayOrders();
        this.findMyOrders();

    }

})