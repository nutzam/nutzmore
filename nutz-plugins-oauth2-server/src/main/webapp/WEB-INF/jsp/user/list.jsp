<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="../top.jsp"/>
</head>
<body>

<div class="container">

    <div class="header clearfix">
        <nav>
            <ul class="nav nav-pills pull-right">
            </ul>
        </nav>
        <h3 class="text-muted">OAuth2 Server 用户列表</h3>
    </div>

    <c:if test="${not empty msg}">
        <div class="alert alert-danger" role="alert">${msg}</div>
    </c:if>

    <h3><a href="${pageContext.request.contextPath}/user/create">用户新增</a></h3>

    <table class="table table-bordered table-hover table-condensed">
        <thead>
        <tr>
            <th>用户名</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${userList}" var="user">
            <tr>
                <td>${user.username}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/user/${user.id}/update">修改</a>
                    <a href="${pageContext.request.contextPath}/user/${user.id}/delete">删除</a>
                    <a href="${pageContext.request.contextPath}/user/${user.id}/changePassword">改密</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
<jsp:include page="../footer.jsp"/>