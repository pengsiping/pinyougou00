var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        brandOptions: [],//绑定品牌列表下拉框数据的
        specOptions: [{id: 1, text: "机身内存"}],//绑定规格的数据列表，
        entity: {customAttributeItems: []},
        ids: []
    },
    methods: {
        findPage: function () {
            var that = this;
            axios.get('/typeTemplate/findPage.shtml', {
                params: {
                    pageNo: this.pageNo
                }
            }).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data.list;
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            }).catch(function (error) {

            })
        },
        //该方法只要不在生命周期的
        add: function () {
            axios.post('/typeTemplate/add.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    alert("添加成功");
                    app.entity= {customAttributeItems: []}
                }
            }).catch(function (error) {
                alert("添加失败")
            });
        },
        //页面加载的时候调用 发送请求 获取所有的品牌列表数据  赋值给变量 brandOptions  要求：[{id:"",text:""}]
        findBrandIds: function () {
            axios.get('/brand/findAll.shtml').then(
                function (response) {
                    //response.data=[{id:,name:,firstchar:}]
                    //[{id:1,text:"联想"},{id:2,text:"华为"}]
                    for (var i = 0; i < response.data.length; i++) {
                        var obj = response.data[i];//{id:,name:,firstchar:}
                        app.brandOptions.push({"id": obj.id, "text": obj.name});
                    }

                }
            )
        },
        //页面加载的时候调用 发送请求 获取所有的规格的数据列表  赋值给变量 specOptions  要求：[{id:"",text:""}]
        findSpecList: function () {
            axios.get('/specification/findAll.shtml').then(
                function (response) {
                    //response.data=[{id:,specName:}]//规格的列表
                    //[{id:1,text:"机身内存"}]
                    for (var i = 0; i < response.data.length; i++) {
                        var obj = response.data[i];//{id:,specName:}
                        app.specOptions.push({"id": obj.id, "text": obj.specName});
                    }

                }
            )
        },
        //向数组中添加js对象
        addTableRow: function () {
            this.entity.customAttributeItems.push({});
        },
        removeTableRow: function (index) {
            this.entity.customAttributeItems.splice(index, 1);
        },


    },
    //钩子函数 初始化了事件和
    created: function () {
        this.findBrandIds();
        this.findSpecList()

    }

})
