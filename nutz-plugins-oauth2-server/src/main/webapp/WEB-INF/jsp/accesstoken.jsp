<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="top.jsp"/>
    <title>OAuth2 Server</title>
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

    <div class="row marketing">
        <div class="col-lg-10">
            <form class="form-horizontal" method="post" action="${base}/accessToken">
                <div class="form-group">
                    <label for="client_id" class="col-sm-4 control-label">应用id</label>
                    <div class="col-sm-8">
                        <input type="text" class="form-control" id="client_id" name="client_id" placeholder="应用id">
                    </div>
                </div>
                <div class="form-group">
                    <label for="client_secret" class="col-sm-4 control-label">应用secret</label>
                    <div class="col-sm-8">
                        <input type="text" class="form-control" id="client_secret" name="client_secret" placeholder="应用secret">
                    </div>
                </div>
                <div class="form-group">
                    <label for="grant_type" class="col-sm-4 control-label">grant_type</label>
                    <div class="col-sm-8">
                        <input type="text" class="form-control" id="grant_type" name="grant_type" placeholder="grant_type" value="authorization_code" readonly>
                    </div>
                </div>
                <div class="form-group">
                    <label for="code" class="col-sm-4 control-label">授权码</label>
                    <div class="col-sm-8">
                        <input type="text" class="form-control" id="code" name="code" placeholder="授权码">
                    </div>
                </div>
                <div class="form-group">
                    <label for="redirect_uri" class="col-sm-4 control-label">回调地址</label>
                    <div class="col-sm-8">
                        <input type="text" class="form-control" id="redirect_uri" name="redirect_uri" placeholder="回调地址">
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-offset-4 col-sm-8">
                        <button type="submit" class="btn btn-default">提交</button>
                    </div>
                </div>
            </form>
        </div>
    </div>

<jsp:include page="footer.jsp"/>