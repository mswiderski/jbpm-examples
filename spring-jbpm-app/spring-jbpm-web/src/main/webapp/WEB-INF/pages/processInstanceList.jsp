<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<body>
	<c:if test="${not empty processInstances}">
			<div style="margin: 10px;">
				<h4>Available process instances</h4>
				<hr />
				<table style="width: 600px" class="reference">
					<tbody>
						<tr>
							<th>Nb</th>
							<th>Name</th>
							<th>Version</th>
							<th>State</th>
							<th>Date</th>
							<th>Deployment</th>
							<th>Initiator</th>
						</tr>
						<c:forEach var="processInstance" items="${processInstances}"
							varStatus="loopCounter">
							<tr>
								<td><c:out value="${loopCounter.count}" /></td>
								<td><a href="show?id=${processInstance.id}"><c:out value="${processInstance.processName}" /></a></td>
								<td><c:out value="${processInstance.processVersion}" /></td>
								<td>
									<c:choose>
										<c:when test="${processInstance.state == 1}">Active</c:when>
										<c:when test="${processInstance.state == 2}">Completed</c:when>
										<c:when test="${processInstance.state == 3}">Aborted</c:when>
										<c:otherwise>Unknown</c:otherwise>
									</c:choose>
								</td>
								<td><c:out value="${processInstance.dataTimeStamp}" /></td>
								<td><c:out value="${processInstance.deploymentId}" /></td>
								<td><c:out value="${processInstance.initiator}" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
	</c:if>
	<c:if test="${empty processInstances}">
		<h4>No process instances available</h4>
		<hr />
	</c:if>
</body>
</html>