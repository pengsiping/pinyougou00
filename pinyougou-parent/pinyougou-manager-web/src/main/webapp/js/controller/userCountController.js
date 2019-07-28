var app = new Vue({
    el: "#app",
    data: {
        list:[],
        entity:{}
    },
    methods: {
        //查询所有品牌列表
        findAll:function () {
            axios.get().then(function (response) {
                console.log(response);
                app.list=response.data;
            })
        },
    },
    created: function () {


    }
})
