<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../top.jsp"/>
</head>
<body>

<div class="container">

    <div class="header clearfix">
        <nav>
            <ul class="nav nav-pills pull-right">
            </ul>
        </nav>
        <h3 class="text-muted">OAuth2 Server 用户</h3>
    </div>

    <form method="post" class="form-inline">
        <div class="form-group">
            <label for="newPassword">新密码：</label>
            <input type="text" id="newPassword" name="newPassword"/>
        </div>
        <input type="submit" value="${op}" class="btn btn-default">
    </form>

<jsp:include page="../footer.jsp"/>