<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<html>
<body>
<h2>Hello World1!</h2>
<h2>用户名：<%=request.getRemoteUser()%></h2>
<a href="http://localhost:9400/cas/logout?service=http://www.baidu.com">退出登录</a>
</body>
</html>
