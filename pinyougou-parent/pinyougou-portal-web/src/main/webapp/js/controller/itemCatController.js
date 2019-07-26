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
        searchEntity: {},
        itemCatList:[]
    },
    methods: {


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
                app.itemCatList = response.data;
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


    },
    //钩子函数 初始化了事件和
    created: function () {

       // this.searchList(1);
         this.findParentId(0);
       /* this.searchList({id:0});
        this.findAll();*/

    }

})
