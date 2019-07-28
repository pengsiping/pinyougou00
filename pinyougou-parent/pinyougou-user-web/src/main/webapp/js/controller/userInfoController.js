var app = new Vue({
    el: "#app",
    data: {
        userInfo:{birthday:'',job:'',headPic:''},
        name:'',
        year:'',
        month:'',
        day:'',
        image:''
    },
    methods:{

        getName:function(){
            alert("get");
            axios.get("login/getName.shtml").then(function (response) {
                app.name=response.data;
            })
        },
        addUserInfo:function () {
            app.userInfo.birthday = app.year+'/'+app.month+'/'+app.day;
            axios.post("/user/addUserInfo.shtml",app.userInfo).then(function (response) {
                if (response.data) {
                    alert(response.data.message)
                }else {
                    alert(response.data.message)
                }
            })
        },
        chengxuyuan:function () {
            app.userInfo.job="程序员";
        },
        manager:function () {
            app.userInfo.job = "经理";
        },
        desire:function () {
            app.userInfo.job="UI设计师";
        },
        //图片上传
        upload:function () {
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
                    app.image = response.data.message;
                    alert(123)
                } else {
                    alert(response.data.message);
                }
            })
        },
        //给对象中图片赋值
        addImage:function () {
            this.userInfo.headPic = this.image;
        }
    },

    created:function () {
        this.getName();

    }
})