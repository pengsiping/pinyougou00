var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],

        searchEntity:{},
        cartList:[],
        totalNum:0,
        totalPrices:0,
        AddressList:[],
        address:{},
        order:{paymentType:1}
    },
    methods: {
        findCartList:function(){
            axios.get("cart/findCartList.shtml").then(function (response) {
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
        },

        addCartList:function(itemId,num){
            axios.get("cart/add.shtml?itemId="+itemId+"&num="+num).then(function (response) {

                app.findCartList();
            })
        },

        findAddressList:function(){
            axios.get("address/findAddressList.shtml").then(function (response) {
                app.AddressList=response.data;
                for (var i = 0; i < response.data.length; i++) {
                    if(response.data[i].isDefault=="1"){
                        app.address= response.data[i];
                    }
                }
            })
        },

        addAddress:function(){
            axios.post("address/add.shtml",this.address).then(function (response) {
                alert(response.data.success)
                if(response.data.success){
                    app.findAddressList();
                }else{
                    alert(response.data.message);
                }

            })
        },

        updateAddress:function(){
            axios.post("address/update.shtml",this.address).then(function (response) {
                if(response.data.success){
                    this.findAddressList();
                }else{
                    alert(response.data.message);
                }

            })
        },

        save:function () {
            if(this.address.id!=null){
                this.updateAddress();
            }else{
                this.addAddress();
            }
        },



        deleteAddress:function(id){
            axios.get("address/delete/"+id+".shtml").then(function (response) {
                if(response.data.success){
                    this.findAddressList();
                }
            })
        },

        selected:function(address){
            this.address=address;
        },

        isSelected:function(address){
            if(this.address==address){
                return true;
            }
            return null;
        },

        //支付方式
        selectPayType:function(type){
            this.$set(this.order,'paymentType',type);
           // this.order.paymentType=type;
        },
        submitOrder:function(){


            /*
            赋值无效
            this.order.receiverAreaName=this.address.address;
            this.order.receiverMobile=this.address.mobile;
            this.order.receiver=this.order.contact;*/

            this.$set(this.order,'receiverAreaName',this.address.address);
            this.$set(this.order,'receiverMobile',this.address.mobile);
            this.$set(this.order,'receiver',this.address.contact);
            axios.post("order/add.shtml",this.order).then(function (response) {
                if(response.data.success){
                    window.location.href="pay.html"
                }else{
                    alert("错误")
                }

            })
        }


    },
    //钩子函数 初始化了事件
    created: function () {
        this.findCartList();

        //判断如果是getOrderInfo.html的时候才加载
        var href =window.location.href
        if(href.indexOf("getOrderInfo.html")!=-1){
            this.findAddressList();
        }

    }

})