/*
test.tpl
*/
SELECT 1 FROM 
<%if(vars.a > 5){ %>
	@a
<%}else{ %>
	$b
<%}%>