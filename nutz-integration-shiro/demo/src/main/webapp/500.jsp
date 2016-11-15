<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isErrorPage="true"%>
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
<title>500 - Error occured</title>
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

<body class=" page-500-full-page">
	<div class="row">
		<div class="col-md-12 page-500">
			<div class=" number font-red">500</div>
			<div class=" details">
				<h3>Oops! Something went wrong.</h3>
				<p>
					We are fixing it! Please come back in a while. <br />
				</p>
				<div class="row">
					<div class="col-md-6">
						<a href="<%=basePath%>" class="btn red btn-outline"> Return home </a> 
					</div>
					<div class="col-md-6">
						<a href="javascript:showMore();" data-error="<%= exception == null ? "" :exception.getMessage() %>" class="btn red btn-outline"> more info </a> 
					</div>
				</div>
			</div>
		</div>
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
	<script type="text/javascript">
		function showMore(){
			alert($(this).data('error'));//TODO show error info
		}
	</script>
</body>
</html>