<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>
	<h1>Deployment unit details</h1>

	<table>
		<tr>
			<td><b>Name</b></td>
			<td><b>Value</b></td>
		</tr>
		<tr>
			<td><b>Identifier</b></td>
			<td>${unit}</td>
		</tr>
		<tr>
			<td><b>Strategy</b></td>
			<td>${strategy}</td>
		</tr>
		<tr>
			<td><b>Classes</b></td>
			<td>
				<br/>
				<br/>
				<table class="reference" style="width: 300px" >
					<tbody>
						<c:forEach var="clazz" items="${classes}">
							<tr>
								<td><c:out value="${clazz}" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</td>
		</tr>
		<tr>
			<td><b>Processes</b></td>
			<td>
				<br/>
				<br/>
				<table class="reference" style="width: 300px" >
					<tbody>
						<tr>
							<th>Id</th>
							<th>Name</th>
							<th>Version</th>
						</tr>
						<c:forEach var="asset" items="${assets}">
							<tr>
								<td><c:out value="${asset.id}" /></td>
								<td><c:out value="${asset.name}" /></td>
								<td><c:out value="${asset.version}" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</td>
		</tr>
	</table>
</body>
</html>