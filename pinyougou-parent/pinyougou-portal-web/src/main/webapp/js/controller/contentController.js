var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        ids: [],
        entity: {parentId:0,name:""},
        entity_1: {},
        entity_2: {},
        searchEntity: {},
        contentList: [],
        itemCatList:[],
        floorTitleList:[],
        flag:false,
        keyword: ''
    },
    methods: {
        findFloorTitle:function (parentId) {
            axios.get('/itemCat/findFloorTitle/' + parentId + '.shtml').then(function (response) {
                app.floorTitleList = response.data;

            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        toggle1:function(){
            this.flag=true;
        },
        toggle2:function(){
            this.flag=false;
        },

        searchItemCatList: function (p_entity) {
            if (this.grade == 1) {
                this.entity_1 = {};
                this.entity_2 = {};
            }
            if(this.grade==2){
                this.entity_1=p_entity;
                this.entity_2={};
            }
            if(this.grade==3){
                this.entity_2=p_entity;
            }
            this.findParentId(p_entity.id);

        },

        findParentId: function (parentId) {
            axios.get('/itemCat/findByParentId/' + parentId + '.shtml').then(function (response) {
                app.itemCatList = response.data;
                app.entity.parentId=parentId;

            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        searchList:function (curPage) {
            axios.post('/content/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据
                app.list=response.data.list;

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll:function () {
            console.log(app);
            axios.get('/content/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
        findPage:function () {
            var that = this;
            axios.get('/content/findPage.shtml',{params:{
                pageNo:this.pageNo
            }}).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data.list;
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            }).catch(function (error) {

            })
        },
        //该方法只要不在生命周期的
        add:function () {
            axios.post('/content/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/content/update.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save:function () {
            if(this.entity.id!=null){
                this.update();
            }else{
                this.add();
            }
        },
        findOne:function (id) {
            axios.get('/content/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/content/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        findAllCategory:function (category) {
            axios.get('/content/findAllCategory/'+category+'.shtml').then(function (response) {
                app.contentList = response.data;
            }).catch(function (error) {
                console.log(error);
            })
        },
        doSearch:function(){
            window.location.href="http://localhost:9104/search.html?keyword="+encodeURIComponent(this.keyword);
        }



    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.findAllCategory(1);
        this.searchItemCatList({id:0})

        this.findFloorTitle(0);
    }

})
