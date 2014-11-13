<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<body>
	<h4>Deploy unit</h4>
	<hr />
	<form method="post" action="deploy">
		<table style="width: 600px" class="reference">
			<tbody>
				<tr>
					<td>Group:Artefact:Version</td>
					<td><input type="text" name="id" /></td>
				</tr>
			</tbody>
		</table>
		<input type="submit" value="Deploy" />
	</form>
</body>
</html>