<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String basePath = request.getContextPath();
%>
<!DOCTYPE html>
<!--[if IE 8]> <html lang="en" class="ie8 no-js"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9 no-js"> <![endif]-->
<!--[if !IE]><!-->
<html lang="en">
<!--<![endif]-->

<head>
<meta charset="utf-8" />
<title>403 - Permission denied</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta content="width=device-width, initial-scale=1" name="viewport" />
<meta content="" name="description" />
<meta content="" name="author" />
<link href="<%=basePath%>/assets/metronic/global/plugins/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>/assets/metronic/global/plugins/simple-line-icons/simple-line-icons.min.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>/assets/metronic/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>/assets/metronic/global/plugins/uniform/css/uniform.default.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>/assets/metronic/global/plugins/bootstrap-switch/css/bootstrap-switch.min.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>/assets/metronic/global/css/components-md.min.css" rel="stylesheet" id="style_components" type="text/css" />
<link href="<%=basePath%>/assets/metronic/global/css/plugins-md.min.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>/assets/metronic/pages/css/error.min.css" rel="stylesheet" type="text/css" />
<link rel="shortcut icon" href="<%=basePath%>/assets/spring-thunder/images/spring-thunder.ico" />
</head>

<body class=" page-404-3">
	<div class="page-inner">
		<img src="<%=basePath%>/assets/metronic/pages/media/pages/earth.jpg" class="img-responsive" alt="">
	</div>
	<div class="container error-404">
		<h1>403</h1>
		<h2>Houston, we have a problem.</h2>
		<p>Actually, the page you are looking for is not permit you to visite.</p>
		<p>
			<a href="<%=basePath%>" class="btn red btn-outline"> Return home </a> <br>
		</p>
	</div>
	<!--[if lt IE 9]>
<script src="<%=basePath%>/assets/metronic/global/plugins/respond.min.js"></script>
<script src="<%=basePath%>/assets/metronic/global/plugins/excanvas.min.js"></script> 
<![endif]-->
	<script src="<%=basePath%>/assets/metronic/global/plugins/jquery.min.js" type="text/javascript"></script>
	<script src="<%=basePath%>/assets/metronic/global/plugins/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
	<script src="<%=basePath%>/assets/metronic/global/plugins/js.cookie.min.js" type="text/javascript"></script>
	<script src="<%=basePath%>/assets/metronic/global/plugins/bootstrap-hover-dropdown/bootstrap-hover-dropdown.min.js" type="text/javascript"></script>
	<script src="<%=basePath%>/assets/metronic/global/plugins/jquery-slimscroll/jquery.slimscroll.min.js" type="text/javascript"></script>
	<script src="<%=basePath%>/assets/metronic/global/plugins/jquery.blockui.min.js" type="text/javascript"></script>
	<script src="<%=basePath%>/assets/metronic/global/plugins/uniform/jquery.uniform.min.js" type="text/javascript"></script>
	<script src="<%=basePath%>/assets/metronic/global/plugins/bootstrap-switch/js/bootstrap-switch.min.js" type="text/javascript"></script>
	<script src="<%=basePath%>/assets/metronic/global/scripts/app.min.js" type="text/javascript"></script>
	
</body>
<script type="text/javascript">
	$(function() {
		setTimeout(function() {
			location.href = '<%=basePath%>';
		}, 5000);
	})
</script>
</html>