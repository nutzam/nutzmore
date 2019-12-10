/*
test.tpl
*/
SELECT  
<% if(vars.a > 5){ %>
	$a
<% }else{ %>
	@b
<% } %>