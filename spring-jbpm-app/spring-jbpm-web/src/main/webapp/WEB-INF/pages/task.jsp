<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>
	<h1>Task details</h1>

	<table>
		<tr>
			<td><b>Task Id</b></td>
			<td>${task.taskId}</td>
		</tr>
		<tr>
			<td><b>Task Name</b></td>
			<td>${task.name}</td>
		</tr>
		<tr>
			<td><b>Task Description</b></td>
			<td>${task.description}</td>
		</tr>
		<tr>
			<td><b>Task Status</b></td>
			<td>${task.status}</td>
		</tr>
		<tr>
			<td><b>Created By</b></td>
			<td>${task.createdBy}</td>
		</tr>
		<tr>
			<td><b>Created On</b></td>
			<td>${task.createdOn}</td>
		</tr>
		<tr>
			<td><b>Actual Owner</b></td>
			<td>${task.actualOwner}</td>
		</tr>
		<tr>
			<td><b>Process Id</b></td>
			<td>${task.processId}</td>
		</tr>
		<tr>
			<td><b>Process Instance Id</b></td>
			<td>${task.processInstanceId}</td>
		</tr>
	</table>
	<h4>Data inputs</h4>
	<hr />
	<table style="width: 600px" class="reference">
		<tbody>
			<tr>
				<th>Name</th>
				<th>Value</th>
			</tr>
			<c:forEach var="entry" items="${inputs}">
				<tr>
					<td><c:out value="${entry.value}" /></td>
					<td><c:out value="${inputValues[entry.value]}" /></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>

	<c:if test="${task.status eq 'Completed'}">
		<h4>Data outputs</h4>
		<hr />
		<table style="width: 600px" class="reference">
			<tbody>
				<tr>
					<th>Name</th>
					<th>Value</th>
				</tr>
				<c:forEach var="entry" items="${outputs}">
					<tr>
						<td><c:out value="${entry.value}" /></td>
						<td><c:out value="${outputValues[entry.value]}" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>

	<c:if test="${task.status eq 'Ready'}">
		<form method="post" action="claim">
			<input type="hidden" name="id" value="${task.taskId}" />

			<hr />
			<input type="submit" name="claim" value="Claim" />
		</form>
	</c:if>
	<c:if test="${task.status eq 'Reserved'}">
		<form method="post" action="start">
			<input type="hidden" name="id" value="${task.taskId}" />

			<hr />
			<input type="submit" name="start" value="Start" />
		</form>
		<form method="post" action="release">
			<input type="hidden" name="id" value="${task.taskId}" />

			<hr />
			<input type="submit" name="release" value="Release" />
		</form>
	</c:if>
	<c:if test="${task.status eq 'InProgress'}">
		<h4>Data outputs</h4>
		<hr />
		<form method="post" action="complete">
			<input type="hidden" name="id" value="${task.taskId}" />
			<table style="width: 600px" class="reference">
				<tbody>
					<tr>
						<th>Name</th>
						<th>Value</th>
					</tr>
					<c:forEach var="entry" items="${outputs}">
						<tr>
							<td><c:out value="${entry.value}" /></td>
							<td><input type="text" name="${entry.value}"
								value="${outputValues[entry.value]}" /></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<input type="submit" name="complete" value="Complete" />
		</form>
	</c:if>
</body>
</html>