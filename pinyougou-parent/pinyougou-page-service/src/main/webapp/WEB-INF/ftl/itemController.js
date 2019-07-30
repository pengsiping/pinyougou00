var app = new Vue({
    el: "#app",
    data: {
        num:1,
        //specificationItems:{},
       specificationItems:JSON.parse(JSON.stringify(sku[0].spec)), //浅克隆:值赋给变量后,变量后续值的变化会影响sku,深克隆可以避免该情况
        sku:sku[0],
       /* specificationItems:JSON.parse(JSON.stringify(skuList[0].spec)),//默认展示第一个数组元素对应的规格数据*/
        //sku1:sku[0]//数组第一个元素就是sku的数据展
    },
    methods: {
        increase:function (num) {
            this.num+=num;
            if(this.num<1){
                this.num=1;
            }
        },
        selectSpec:function(name,value){
            //this.specificationItems[name]=value;
            //this.$set(this.specificationItems,name,value);
            this.$set(this.specificationItems,name,value)
            this.search();

        },

        isSelect:function (name,value) {
            if(this.specificationItems[name]==value){
                return true;
            }
            return false;
        },

        search:function(){
            for (var i = 0; i <sku.length ; i++) {
                var obj=sku[i];
                    if(JSON.stringify(this.specificationItems)==JSON.stringify(sku[i].spec)){
                        app.sku=obj;
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
        //静态页面生成的加载显示发送此商品的id给后台做我的足迹记录的函数
        findMyFootprint:function (goodsId) {
            //发送跨域请求到user查询用户中心的我的足迹列表信息
            axios.get('/user/myFootprint.shtml',{
                params:{
                    goodsId:goodsId
                },
                //客户端在ajax的时候也要携带cookie到服务器
                withCredentials:true
            }).then(
                function (response) {
                    //页面加载成功，判断浏览是否成功加入redis中
                    if(response.data.success){
                        //如果成功
                        response.data.message;
                    }else {
                        //如果失败
                        response.data.message;
                    }
                }
            )
        }



    },
    //钩子函数 初始化了事件
    created: function () {
        //页面加载的时候向后台发送itemId(生成的所有静态页面)的参数
        var urlParam = this.getUrlParam();

        var goodsId = urlParam.goodsId;

        //页面加载调用此方法
        this.findMyFootprint(goodsId);
    }

})
