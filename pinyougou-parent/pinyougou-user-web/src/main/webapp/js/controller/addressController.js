var app = new Vue({
    el: "#app",
    data: {
        addresses:[],
        name:'',
        item:{'contact':'','provinceId':'','cityId':'','townId':'','address':'','mobile':'','alias':''},//'provinceId':'','cityId':'','townId':'',,'alias':''
        address:{}
    },
    methods:{
        //地址管理查询
        findAddress:function () {
            axios.get("/user/findAddress.shtml").then(function (response) {
                app.addresses=response.data;
                app.getName();
            })
        },
        getName:function(){
            axios.get("login/getName.shtml").then(function (response) {

                app.name=response.data;
            })
        },
        deleteAddress:function (index) {
            axios.get("/user/deleteAddress.shtml?index="+index).then(function (response) {
                if (response.data) {
                    app.findAddress();
                }
            })
        },
        addAddress:function () {
            axios.post("/user/addAddress.shtml",app.item).then(function (response) {
                if (response.data) {
                    app.findAddress();
                }else {
                    alert(response.data.message)
                }
            })
        },
        setDefaultAddress:function (index) {
            axios.get("/user/setDefaultAddress.shtml?index="+index).then(function (response) {
                app.findAddress();
            })
        },
        home:function () {
            app.item.alias="家里"
        },
        parentsHome:function () {
            app.item.alias = "父母家"
        },
        office:function () {
            app.item.alias="公司"
        },
        findOneAddress:function (id) {
            axios.get("/user/findOneAddress.shtml?id="+id).then(function (response) {
                app.item = response.data;
            })
        },
        updateAddress:function () {
            axios.post("/user/updateAddress.shtml",app.item).then(function (response) {
                if (response.data) {
                    app.findAddress();
                }else {
                    alert(response.data.message)
                }
            })
        },
        saveAddress:function () {
            if (app.item.id!=null) {
                this.updateAddress();
            }else {
                this.addAddress();
            }
        }
    },

    created:function () {
        this.findAddress();

    }
})