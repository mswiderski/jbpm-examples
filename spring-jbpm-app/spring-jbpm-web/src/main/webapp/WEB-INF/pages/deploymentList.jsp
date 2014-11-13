<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<body>

	<c:if test="${not empty deployedUnits}">
		<form method="post" action="undeploy">
			<div style="margin: 10px;">
				<h4>Available deployment units</h4>
				<hr />
				<table style="width: 600px" class="reference">
					<tbody>
						<tr>
							<th>Nb</th>
							<th>Select</th>
							<th>Identifier</th>
						</tr>
						<c:forEach var="unit" items="${deployedUnits}"
							varStatus="loopCounter">
							<tr>
								<td><c:out value="${loopCounter.count}" /></td>
								<td><input type="radio" name="id" value="${unit}" /></td>
								<td><a href="show?id=${unit}"><c:out value="${unit}" /></a></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<input type="submit" value="Undeploy selected" />
		</form>
	</c:if>
	<c:if test="${empty deployedUnits}">
		<h4>No deployment units available</h4>
		<hr />
	</c:if>
</body>
</html>