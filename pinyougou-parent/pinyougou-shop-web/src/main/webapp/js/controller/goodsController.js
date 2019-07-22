var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {
            tbGoods: {},
            tbGoodsDesc: {customAttributeItems: [], itemImages: [], specificationItems: []},
            tbItemList: []
        },
        ids: [],
        searchEntity: {},
        image_entity: {url: "", color: ""},
        itemCatList1: [],
        itemCatList2: [],
        itemCatList3: [],
        brandTextList: [],
        specList: []



    },
    methods: {

        findItemCatList1: function (parentId) {
            axios.get("/itemCat/findByParentId/" + parentId + ".shtml").then(function (response) {
                app.itemCatList1 = response.data;
            })
        },

        searchList: function (curPage) {
            axios.post('/goods/search.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
                //获取数据
                app.list = response.data.list;

                //当前页
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll: function () {
            console.log(app);
            axios.get('/goods/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data;

            }).catch(function (error) {

            })
        },
        findPage: function () {
            var that = this;
            axios.get('/goods/findPage.shtml', {
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

            this.entity.tbGoodsDesc.introduction = editor.html();

            axios.post('/goods/add.shtml', this.entity).then(function (response) {
                alert(editor.html())
                if (response.data.success) {
                    /*app.entity={tbGoods:{},tbGoodsDesc:{},tbItems:[]}
                    editor.html("");*/
                    alert("成功");
                }
            }).catch(function (error) {
                console.log("1231312131321HHH");
            });
        },
        update: function () {

            this.entity.tbGoodsDesc.introduction = editor.html();
            axios.post('/goods/update.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save: function () {
            if (this.entity.tbGoods.id != null) {
                this.update();
            } else {
                this.add();
            }
        },
        findOne: function (id) {
            axios.get('/goods/findOne/' + id + '.shtml').then(function (response) {
                app.entity = response.data;
                editor.html(app.entity.tbGoodsDesc.introduction);
                app.entity.tbGoodsDesc.itemImages=JSON.parse(app.entity.tbGoodsDesc.itemImages);
                app.entity.tbGoodsDesc.customAttributeItems=JSON.parse(app.entity.tbGoodsDesc.customAttributeItems);
                app.entity.tbGoodsDesc.specificationItems=JSON.parse(app.entity.tbGoodsDesc.specificationItems);

                for (var i = 0; i <app.entity.tbItemList.length ; i++) {
                    var item =app.entity.tbItemList[i];
                    item.spec= JSON.parse(item.spec);
                }


            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele: function () {
            axios.post('/goods/delete.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        upload: function () {
            var formData = new FormData();
            formData.append('file', file.files[0]);
            axios({
                url: "http://localhost:9110/upload/uploadFile.shtml",
                data: formData,
                method: 'post',
                headers: {
                    "Content-Type": "multipart/form-data"
                },
                /*支持跨域请求携带相关信息*/
                withCredentials: true
            }).then(function (response) {
                if (response.data.success) {
                    console.log(this);
                    app.image_entity.url = response.data.message;
                    console.log(JSON.stringify(app.image_entity))
                } else {
                    alert(response.data.message);
                }
            })
        },

        addImageEntity: function () {
            this.entity.tbGoodsDesc.itemImages.push(this.image_entity)
        },

        removeImageEntity: function (index) {
            this.entity.tbGoodsDesc.itemImages.splice(index, 1);
        },
        updateChecked: function ($event, specName, specValue) {
            //1.specName:网络,key:移动3G
            var specificationItem = this.searchObjectByKey(this.entity.tbGoodsDesc.specificationItems, specName, 'attributeName');

            if (specificationItem != null) {

                if ($event.target.checked) {
                    specificationItem.attributeValue.push(specValue);
                } else {
                    specificationItem.attributeValue.splice(specificationItem.attributeValue.indexOf(specValue), 1);
                    //如果该对象没有数据,则移除对象
                    if (specificationItem.attributeValue.length == 0) {
                        this.entity.tbGoodsDesc.specificationItems.splice(this.entity.tbGoodsDesc.specificationItems.indexOf(specificationItem), 1)
                    }
                }

            } else {

                this.entity.tbGoodsDesc.specificationItems.push({
                    "attributeName": specName,  //网络,
                    "attributeValue": [specValue]  //移动3g
                })

            }
        },


        searchObjectByKey: function (list, specName, key) {
            for (var i = 0; i < list.length; i++) {
                var specificationItem = list[i];
                if (specificationItem[key] == specName) {
                    return specificationItem;
                }
            }
            return null;
        },

        createList: function () {
            this.entity.tbItemList = [{'spec': {}, 'price': 0, 'num': 0, 'status': '0', 'isDefault': '0'}];
            //[ { "attributeName": "网络", "attributeValue": [ "移动3G", "联通3G", "移动4G" ] } ]
            //目标--{"网络":"联通4G"}
            var specificationItems = this.entity.tbGoodsDesc.specificationItems;
            for (var i = 0; i < specificationItems.length; i++) {
                var specificationItem=specificationItems[i];
                this.entity.tbItemList = this.addColumn(
                    this.entity.tbItemList,
                    specificationItem.attributeName,
                    specificationItem.attributeValue
                );
            }

        },

        addColumn: function (list,columnName,columnValue) {
            var newList=[];
            for (var i = 0; i < list.length ; i++) {
                var oldList = list[i];
                for (var j = 0; j < columnValue.length; j++) {
                  var newRow = JSON.parse(JSON.stringify(oldList)); //深克隆
                    var value=columnValue[j];
                    newRow.spec[columnName]=value;
                    newList.push(newRow);
                }
            }
            return newList;
        },
        //specName:网络,specValue:移动3G
        isChecked:function(specName,specValue){ //[ { "attributeValue": [ "移动3G" ], "attributeName": "网络" } ]
            var obj = this.searchObjectByKey(this.entity.tbGoodsDesc.specificationItems,specName,"attributeName");
            console.log(obj);
            if (obj!=null){
                if(obj.attributeValue.indexOf(specValue)!=-1){
                    return true;
                }
            }
            return false;
        }


    },
    watch: {
        //newval为新值,oldValue为旧值
        'entity.tbGoods.category1Id': function (newval, oldvalue) {
            if (newval != null) {
                axios.get("/itemCat/findByParentId/" + newval + ".shtml").then(function (response) {
                    app.itemCatList2 = response.data;
                })
            }
        },

        'entity.tbGoods.category2Id': function (newval, oldvalue) {
            if (newval != null) {
                axios.get("/itemCat/findByParentId/" + newval + ".shtml").then(function (response) {
                    app.itemCatList3 = response.data;
                })
            }
        },

        'entity.tbGoods.category3Id': function (newval, oldvalue) {
            if (newval != null) {
                axios.get("/itemCat/findOne/" + newval + ".shtml").then(function (response) {
                    //app.itemCatList3=response.data.typeId;

                    app.$set(app.entity.tbGoods, 'typeTemplateId', response.data.typeId);
                })
            }
        },


        'entity.tbGoods.typeTemplateId': function (newval, oldvalue) {
            if (newval != null) {
                axios.get("/typeTemplate/findOne/" + newval + ".shtml").then(function (response) {
                    var brandList = response.data;
                    app.brandTextList = JSON.parse(brandList.brandIds);
                    /*app.brandTextList=brandList.name; */
                    if(app.entity.tbGoods.id==null){

                        app.entity.tbGoodsDesc.customAttributeItems = JSON.parse(brandList.customAttributeItems);
                    }

                });

                axios.get("/typeTemplate/findSpecList/" + newval + ".shtml").then(function (response) {
                    app.specList = response.data;

                })

            }

        }

        /*'entity.tbGoods.typeTemplateId':function (newval,oldvalue) {
            if(newval!=null){
                axios.get("/typeTemplate/findSpecList/"+newval+".shtml").then(function (response) {
                    app.specList = response.data();

                })
            }

        }*/
    },


    //钩子函数 初始化了事件和
    created: function () {

        this.searchList(1);
        this.findItemCatList1(0);
        var request=this.getUrlParam();
        console.log(request);
        this.findOne(request.id);

    }

})
