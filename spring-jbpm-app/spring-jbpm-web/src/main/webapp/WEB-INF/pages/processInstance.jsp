<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>
	<h1>Process definition details</h1>

	<table>
		<tr>
			<td><b>Process Instance Id</b></td>
			<td>${processInstance.id}</td>
		</tr>
		<tr>
			<td><b>Process Name</b></td>
			<td>${processInstance.processName}</td>
		</tr>
		<tr>
			<td><b>Process Version</b></td>
			<td>${processInstance.processVersion}</td>
		</tr>
		<tr>
			<td><b>Process Description</b></td>
			<td>${processInstance.processInstanceDescription}</td>
		</tr>
		<tr>
			<td><b>State</b></td>
			<td>
				<c:choose>
					<c:when test="${processInstance.state == 1}">Active</c:when>
					<c:when test="${processInstance.state == 2}">Completed</c:when>
					<c:when test="${processInstance.state == 3}">Aborted</c:when>
					<c:otherwise>Unknown</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<td><b>Deployment id</b></td>
			<td>${processInstance.deploymentId}</td>
		</tr>
		<tr>
			<td><b>Date</b></td>
			<td>${processInstance.dataTimeStamp}</td>
		</tr>
	</table>
	<h4>Process Variables</h4>
	<hr />

	<table style="width: 600px" class="reference">
		<tbody>
			<tr>
				<th>Name</th>
				<th>Value</th>
			</tr>
			<c:forEach var="variable" items="${variables}">
				<tr>
					<td><c:out value="${variable.variableId}" /></td>
					<td><c:out value="${variable.newValue}" /></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	
	<c:if test="${processInstance.state == 1}">
		<h4>Abort process instance</h4>
		<form method="post" action="abort">
			<input type="hidden" name="deploymentId" value="${processInstance.deploymentId}" /> 
			<input type="hidden" name="id" value="${processInstance.id}" /> 
				
			<hr />
			<input type="submit" name="start" value="Abort process instance" />
		</form>
		<h4>Signal process instance</h4>
		<form method="post" action="signal">
			<b>Signal:</b> <input type="text" name="signal" /><br/>
			<b>Event:</b> <input type="text" name="data" /><br/> 
			<input type="hidden" name="deploymentId" value="${processInstance.deploymentId}" /> 
			<input type="hidden" name="id" value="${processInstance.id}" /> 
			<hr />
			<input type="submit" name="start" value="Signal process instance" />
		</form>
	</c:if>
</body>
</html>