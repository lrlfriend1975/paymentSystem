<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
</head>

<body onload="sub()">
	<div style="display: none;">
		<input type="hidden" id="urlStr" value="${result.url}" />
		<form id="form1" action="${result.url}" method="get">
		</form>
	</div>
</body>
<script type="text/javascript">
	function sub() {
		window.location.href = document.getElementById("urlStr").value;
	}
</script>
</html>
