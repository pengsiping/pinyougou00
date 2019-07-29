
var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        ids: [],
        cartList:[],
        totalNum:0,
        totalPrices:0
    },
    methods: {
        addCart: function (itemId) {
            axios.get("http://localhost:9107/cart/add.shtml",
                {
                    params: {
                        itemId: itemId,
                        num: 1
                    },
                    withCredentials: true
                }).then(function (response) {
                if (response.data.success) {
                    alert(response.data.message);
                    window.location.href = "http://localhost:9107/cart.html";
                } else {
                    alert("添加失败");
                }

            })
        },
        findCartList:function(){
            axios.get("user/findCartList.shtml").then(function (response) {
                //alert(response.data);
                app.cartList=response.data;
                app.totalNum=0;
                app.totalPrices=0;
                for (var i = 0; i < response.data.length; i++) {
                    var obj = response.data[i]; //Cart
                    for (var j = 0; j < obj.orderItemList.length; j++) {
                        //tbOrderItem
                        var objx=obj.orderItemList[j];
                        app.totalNum+=objx.num;
                        app.totalPrices+=objx.totalFee;
                    }
                }

            })
        }




    },
    //钩子函数 初始化了事件和
    created: function () {

        this.findCartList();

    }

})
