<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2018-03-21
  Time: 9:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>广告上传</title>
</head>
<body>
    <h2>文件上传</h2>
    <form action="${pageContext.request.contextPath}/advertisement/upload" enctype="multipart/form-data" method="post">
        <table>
            <tr>
                <td>广告商编号:</td>
                <td><input type="text" name="id"></td>
            </tr>
            <tr>
                <td>跳转URL:</td>
                <td><input type="text" name="redirectURL"></td>
            </tr>
            <tr>
                <td>文件描述:</td>
                <td><input type="text" name="description"></td>
            </tr>
            <tr>
                <td>请选择文件:</td>
                <td><input type="file" name="file"></td>
            </tr>
            <tr>
                <td><input type="submit" value="上传"></td>
            </tr>
        </table>
    </form>
</body>
</html>
