<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="top.jsp"/>
    <title>OAuth2协议认证登录页面</title>
</head>
<body>
<div class="container">

    <div class="header clearfix">
        <nav>
            <ul class="nav nav-pills pull-right">
            </ul>
        </nav>
        <h3 class="text-muted">OAuth2 Server</h3>
    </div>

    <c:if test="${not empty client.clientName}">
        <span class="label label-info">应用<small>${client.clientName}</small>请求接入开放数据平台</span>
    </c:if>

    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert">${error}</div>
    </c:if>

    <div class="row marketing">
        <div class="col-lg-10">
            <%--<form class="form-horizontal" method="post" action="${pageContext.request.contextPath}/authorize">--%>
             <form class="form-horizontal" method="post" action="">
                <input type="hidden" name="client_id" value="${param.client_id}">
                <input type="hidden" name="response_type" value="${param.response_type}">
                <input type="hidden" name="redirect_uri" value="${param.redirect_uri}">
                <div class="form-group">
                    <label for="username" class="col-sm-4 control-label">用户名</label>
                    <div class="col-sm-8">
                        <input type="text" class="form-control" id="username" name="username" placeholder="用户名">
                    </div>
                </div>
                <div class="form-group">
                    <label for="password" class="col-sm-4 control-label">密码</label>
                    <div class="col-sm-8">
                        <input type="password" class="form-control" id="password" name="password" placeholder="密码">
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-offset-4 col-sm-8">
                        <button type="submit" class="btn btn-default">登录并授权</button>
                    </div>
                </div>
            </form>
        </div>
    </div>

<jsp:include page="footer.jsp"/>