<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<body>
	<c:if test="${not empty processDefinitions}">
			<div style="margin: 10px;">
				<h4>Available process definitions</h4>
				<hr />
				<table style="width: 600px" class="reference">
					<tbody>
						<tr>
							<th>Nb</th>
							<th>Name</th>
							<th>Version</th>
						</tr>
						<c:forEach var="process" items="${processDefinitions}"
							varStatus="loopCounter">
							<tr>
								<td><c:out value="${loopCounter.count}" /></td>
								<td><a href="show?id=${process.id}&deployment=${process.deploymentId}"><c:out value="${process.name}" /></a></td>
								<td><c:out value="${process.version}" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
	</c:if>
	<c:if test="${empty processDefinitions}">
		<h4>No process definitions available</h4>
		<hr />
	</c:if>
</body>
</html>