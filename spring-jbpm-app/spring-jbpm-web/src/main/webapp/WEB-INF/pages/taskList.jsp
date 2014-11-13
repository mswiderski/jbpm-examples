<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<body>
	<c:if test="${not empty tasks}">
			<div style="margin: 10px;">
				<h4>Available tasks</h4>
				<hr />
				<table style="width: 600px" class="reference">
					<tbody>
						<tr>
							<th>Id</th>
							<th>Name</th>
							<th>Description</th>
							<th>State</th>
							<th>Date</th>
							<th>Owner</th>
						</tr>
						<c:forEach var="task" items="${tasks}" varStatus="loopCounter">
							<tr>
								<td><c:out value="${task.id}" /></td>
								<td><a href="show?id=${task.id}"><c:out value="${task.name}" /></a></td>								
								<td><c:out value="${task.description}" /></td>
								<td><c:out value="${task.status}" /></td>
								<td><c:out value="${task.createdOn}" /></td>
								<td><c:out value="${task.actualOwnerId}" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
	</c:if>
	<c:if test="${empty tasks}">
		<h4>No tasks available</h4>
		<hr />
	</c:if>
</body>
</html>