<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Freemarker入门小DEMO</title>
</head>
<body>
<pre>
    <#--我只是一个注释，我不会有任何输出  -->
    ${name},你好。${message}
    <div>
        <#assign linkman="张三">
        联系人：${linkman}
    </div>
    <div>
        <#assign info={"mobile":"13301231212","address":"北京市昌平区王府街"} >
        电话：${info.mobile}  地址：${info.address}
    </div>
    <div>
        <#include "head.ftl">
    </div>
    <div>
        <#if success=true>
            你已通过实名认证
        <#else >
            你未通过实名认证
        </#if>
    </div>
    <div>
        <#list goodsList as good>
            ${good_index+1}--${good.name}---${good.price}
        </#list>
    </div>
    <div>
        集合大小：${goodsList?size}
    </div>
    <div>
        <#assign text="{'bank':'工商银行','account':'123'}">
        <#assign data=text?eval>
        银行：${data.bank}--帐号：${data.account}
    </div>
    <div>

        ${today?date}<br/>
        ${today?time}<br/>
        ${today?datetime}<br/>
        ${today?string("yyyy年MM月dd日HH时mm分ss秒")}
    </div>
    <div>
        包含千分位：${point}
        不包含千分位：${point?c}
    </div>
    <div>
        <h4>判断某变量是否存在</h4>
        <#if aaa??>
            变量存在
        <#else >
            变量不存在
        </#if>
    </div>
    <div>
        ${aaa!"fa"}
    </div>
</pre>
</body>
</html>