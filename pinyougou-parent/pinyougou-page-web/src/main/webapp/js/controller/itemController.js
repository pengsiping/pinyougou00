var app = new Vue({
    el: "#app",
    data: {
        num: 1,
        //specificationItems:{},
        specificationItems: JSON.parse(JSON.stringify(sku[0].spec)), //浅克隆:值赋给变量后,变量后续值的变化会影响sku,深克隆可以避免该情况
        sku: sku[0],
        /* specificationItems:JSON.parse(JSON.stringify(skuList[0].spec)),//默认展示第一个数组元素对应的规格数据*/
        //sku1:sku[0]//数组第一个元素就是sku的数据展
    },
    methods: {
        increase: function (num) {
            this.num += num;
            if (this.num < 1) {
                this.num = 1;
            }
        },
        selectSpec: function (name, value) {
            //this.specificationItems[name]=value;
            //this.$set(this.specificationItems,name,value);
            this.$set(this.specificationItems, name, value)
            this.search();

        },

        isSelect: function (name, value) {
            if (this.specificationItems[name] == value) {
                return true;
            }
            return false;
        },

        search: function () {
            for (var i = 0; i < sku.length; i++) {
                var obj = sku[i];
                if (JSON.stringify(this.specificationItems) == JSON.stringify(sku[i].spec)) {
                    app.sku = obj;
                    break;
                }
            }

            /*  for(var i=0;i<skuList.length;i++){
                  var object = skuList[i];
                  if(JSON.stringify(this.specificationItems)==JSON.stringify(skuList[i].spec)){
                      console.log(object);
                      this.sku=object;
                      break;
                  }
              }*/
        },

        /*addCart:function () {
            axios.get("http://localhost:9107/cart/add.shtml?itemId="+app.sku.id+"&num="+app.num).then(function (response) {
                if(response.data.success){
                    alert(response.data.message);
                    window.location.href="http://localhost:9107/cart.html";
                } else{
                    alert("添加失败");
                }

            })
        }*/

        addCart: function () {
            axios.get("http://localhost:9107/cart/add.shtml",
                {
                    params: {
                        itemId: this.sku.id,
                        num: this.num
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
        }


    },
//钩子函数 初始化了事件
    created: function () {

    }

})
