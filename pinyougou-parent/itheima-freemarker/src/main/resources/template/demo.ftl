<html>
<head>
    <meta charset="utf-8">
    <title>Freemarker入门小DEMO </title>
</head>
<body>
<#--我只是一个注释，我不会有任何输出  -->
${name},你好。${message}

<#assign linkman="周先生">
联系人：${linkman}
<#assign info={"mobile":"13301231212",'address':'北京市昌平区王府街'} >
电话：${info.mobile}  地址：${info.address}

<#include "head.ftl">

<#if success==true>
    通过验证
    <#else >
    未通过验证
</#if>

<#list goodsList as goods>
    ${goods_index+1} 商品名称:${goods.name} 价格${goods.price}
</#list>

共 ${goodsList?size}条记录

//JSON转对象
<#assign text="{'bank':'工商','account':'324234'}">
<#assign data=text?eval >
${data.bank}===${data.account}

//时间输出

当前日期${today?date}
当前时间${today?time}
当前时间+日期${today?datetime}
${today?string("yyyy年MM月")}

//数字去除千分位
${point?c}

//空值处理
<#if aaa??>
    aaa变量存在
    <#else>
        aaa变量不存在
</#if>


${aaa?c!'-'}

</body>
</html>