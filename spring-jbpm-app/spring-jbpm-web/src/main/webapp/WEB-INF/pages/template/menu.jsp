<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<ul style="list-style:none;line-height:28px;">

	<li>
    <spring:url value="/index" var="homeUrl" htmlEscape="true" />
		<a href="${homeUrl}">Home</a>
	</li>

	<li>
    <spring:url value="/deployment/" var="deploymentListUrl" htmlEscape="true" />
		<a href="${deploymentListUrl}">Deployment List</a>
		<li>
    		<spring:url value="/deployment/new" var="newDeployment" htmlEscape="true" />
			&nbsp;&nbsp;<a href="${newDeployment}">New deployment</a>
		</li>
	</li>
	<li>
    <spring:url value="/processdef/" var="processDefList" htmlEscape="true" />
		<a href="${processDefList}">Process Definitions</a>
	</li>
	
	<li>
    <spring:url value="/processinstance/" var="processInstanceList" htmlEscape="true" />
		<a href="${processInstanceList}">Process Instances</a>
	</li>
	
	<li>
    <spring:url value="/task/" var="taskList" htmlEscape="true" />
		<a href="${taskList}">My Tasks</a>
	</li>
	
		<li>
    <spring:url value="/j_spring_security_logout" var="logout" htmlEscape="true" />
		<a href="${logout}">Logout</a>
	</li>
</ul>