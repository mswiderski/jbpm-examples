<html>
<body>
	<h4>Message : ${message}</h4>
	<hr/>
	<form method="get" action="show"> 
		<input type="hidden" name="id" value="${taskId}" /> 
			
		<hr />
		<input type="submit" name="show" value="Back to task" />
	</form>	
</body>
</html>