<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>jBPM Spring Application</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/screen.css" type="text/css" media="screen, projection"></link>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/print.css" type="text/css" media="print"></link>
	<!--[if IE]>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/ie.css"  type="text/css" media="screen, projection">
	<![endif]-->
<style>
body{ margin-top:20px; margin-bottom:20px; background-color:#DFDFDF;}
</style>
</head>
<body>
	<div class="container" style="border: #C1C1C1 solid 1px; border-radius:10px;">
		<!-- Header -->
		<tiles:insertAttribute name="header" />
		<!-- Menu Page -->
		<div class="span-5  border" style="height:400px;background-color:#FCFCFC;">
			<tiles:insertAttribute name="menu" />
		</div>
		<!-- Body Page -->
		<div class="span-19 last">
			<tiles:insertAttribute name="body" />
		</div>
		<!-- Footer Page -->
		<tiles:insertAttribute name="footer" />
	</div>
</body>
</html>