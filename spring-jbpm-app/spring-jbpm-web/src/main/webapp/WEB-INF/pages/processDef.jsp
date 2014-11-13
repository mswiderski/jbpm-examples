<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>
	<h1>Process definitin details</h1>

	<table>
		<tr>
			<td><b>Process Id</b></td>
			<td>${processDefinition.id}</td>
		</tr>
		<tr>
			<td><b>Process Name</b></td>
			<td>${processDefinition.name}</td>
		</tr>
		<tr>
			<td><b>Process Version</b></td>
			<td>${processDefinition.version}</td>
		</tr>
		<tr>
			<td><b>Package</b></td>
			<td>${processDefinition.packageName}</td>
		</tr>
		<tr>
			<td><b>Type</b></td>
			<td>${processDefinition.type}</td>
		</tr>
	</table>
	<h4>Process Variables</h4>
	<hr />
	<form method="post" action="new">
	<input type="hidden" name="deploymentId" value="${processDefinition.deploymentId}" />
	<input type="hidden" name="processId" value="${processDefinition.id}" />
	<table style="width: 600px" class="reference">
		<tbody>
			<tr>
				<th>Name</th>
				<th>Value</th>
			</tr>
			<c:forEach var="entry" items="${processDefinition.processVariables}">
				<tr>
					<td><c:out value="${entry.key}" /> (<c:out value="${entry.value}" />)</td>
					<td><input type="text" name="${entry.key}" /></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<input type="submit" name="start" value="Start process instance"/>
</body>
</html>