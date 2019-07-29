var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {parentId:0,name:"",typeId:""},
        typeOptions:[],
        ids: [],
        entity_1: {},
        entity_2: {},
        grade: 1,
        searchEntity: {}
    },
    methods: {
        searchList: function (p_entity) {
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

         /*searchList:function (curPage) {
             axios.post('/itemCat/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
                 //获取数据
                 app.list=response.data.list;

                 //当前页
                 app.pageNo=curPage;
                 //总页数
                 app.pages=response.data.pages;
             });
         },*/


        //查询所有品牌列表
        findAll: function () {
            console.log(app);
            axios.get('/itemCat/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                //app.list = response.data;
                var typeList = response.data;
                for (var i = 0; i < typeList.length; i++) {
                    app.typeOptions.push({typeId:typeList[i].typeId})
                }



            }).catch(function (error) {

            })
        },
        findPage: function () {
            var that = this;
            axios.get('/itemCat/findPage.shtml', {
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
            axios.post('/itemCat/add.shtml', this.entity).then(function (response) {
                console.log(response);

                if (response.data.success) {
                    app.entity={parentId:app.entity.parentId}
                    app.searchList({id:app.entity.parentId});
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update: function () {
            axios.post('/itemCat/update.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList({id:0});
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save: function () {
            if (this.entity.id != null) {
                this.update();
            } else {
                this.add();
            }
        },



        findOne: function (id) {
            axios.get('/itemCat/findOne/' + id + '.shtml').then(function (response) {
                app.entity = response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        dele: function () {
            axios.post('/itemCat/delete.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList({id:0});
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        findParentId: function (parentId) {
            axios.get('/itemCat/findByParentId/' + parentId + '.shtml').then(function (response) {
                app.list = response.data;
                app.entity.parentId=parentId;

            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        searchList: function (p_entity) {
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
        upload: function () {
            let formData = new window.FormData();
            formData.append('file', document.querySelector('input[type=file]').files[0]);
            axios({
                url: '/itemCat/upload.shtml',
                data: formData,
                method: 'post',
                header: {
                    'Content-Type': 'multipart/form-data'
                }
            }).then((response) => {
                if (response.data.success) window.location.reload();
                else alert(response.data.message);
            })
                .catch((error) => {
                    alert(error)
                })
        }

    },
    //钩子函数 初始化了事件和
    created: function () {

       // this.searchList(1);
        // this.findParentId(0);
        this.searchList({id:0});
        this.findAll();

    }

})
