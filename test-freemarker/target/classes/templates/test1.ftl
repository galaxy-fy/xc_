<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta
     charset="utf‐8">
    <title>Hello World!</title>
</head>
<body>
Hello ${name}!
<br/>
遍历数据模型中的stus(List集合数据)
<table>
    <tr>
        <th>序号</th>
        <th>姓名</th>
        <th>年龄</th>
        <th>钱包</th>
        <th>生日</th>
    </tr>
<#if stus ??>
    <#list stus as stu>
        <tr>
            <td>${stu_index+1}</td>
            <td <#if stu.name=="小明">style="background: cornflowerblue;" </#if>>${stu.name}</td>
            <td>${stu.age}</td>
            <td <#if (stu.money>300)>style="background: cornflowerblue;" </#if>>${stu.money}</td>
            <td>${stu.birthday?string("yyyy年MM月")}</td>
            <#--<td>${stu.birthday?date}</td>
            <td>${stu.birthday?datetime}</td>
            <td>${stu.birthday?time}</td>-->
        </tr>

    </#list>
</#if>
</table>
<br/>
遍历数据模型中的stuMap(Map数据)
<br/>
<br/>
第一种方法:在中括号中填写map的key
<br/>
<br/>
姓名:${stuMap["stu1"].name}<br/>
年龄:${stuMap["stu1"].age}<br/>
<br/>
第二种方法:在map 的后边直接加 "点 key"
<br/>
姓名:${stuMap.stu1.name}<br/>
年龄:${stuMap.stu1.age}<br/>
姓名:${(stuMap.stu2.name)!""}<br/>
年龄:${(stuMap.stu2.age)!""}<br/>
<br/>
第三种方法:当做list来遍历
stuMap?keys就是key列表(拿到了map集合中的key列表)
<br/>
<#list stuMap?keys as k>
姓名:${stuMap[k].name}<br/>
年龄:${stuMap[k].age}<br/>
</#list>
<br/>
取到的point的值为:${point?c}
</br>
把json字符串转换成对象
<#assign text="{'bank':'工商银行','account':'10101920201920212'}" />
    <#assign data=text?eval />
开户行：${data.bank}  账号：${data.account}
</body>
</html>