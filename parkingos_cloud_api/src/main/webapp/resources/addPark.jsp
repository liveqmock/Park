<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2018-03-21
  Time: 13:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>广告商绑定停车场</title>
</head>
<body>

    <h2>广告商绑定停车场</h2>
    <form action="${pageContext.request.contextPath}/advertisement/addParkId"  method="post">
        <table>
            <tr>
                <td>广告商编号:</td>
                <td><input type="text" name="advertiserId"></td>
            </tr>
            <tr>
                <td>停车场编号:</td>
                <td><input type="text" name="parkId"></td>
            </tr>
            <tr>
                <td><input type="submit" value="提交"></td>
            </tr>
        </table>
    </form>

</body>
</html>
