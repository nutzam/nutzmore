<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
            <h3 class="text-muted">OAuth2 Server 应用</h3>
        </div>
        <form  method="post" commandName="client" cssClass="form-inline">
            <hidden name="id"/>
            <hidden name="clientId"/>
            <hidden name="clientSecret"/>
        <div class="form-group">
            <label>应用名：</label>
            <input type="text" name="clientName"/>
        </div>
		<input type="button" value="${op}" />
        </form >
<jsp:include page="../footer.jsp"/>