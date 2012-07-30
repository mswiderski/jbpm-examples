<html>
<body>
<h2>second</h2>
<hr>
<#if task.descriptions[0]??>
Description: ${task.descriptions[0].text}<BR/>
</#if>
<#if task.taskData??>
Actual owner is: ${task.taskData.actualOwner.id}<BR/>
</#if>
<#list output as entry>  
  response from service is ${entry}<BR/>
</#list>

<form action="complete" method="POST" enctype="multipart/form-data">
<BR/>
<input type="submit" name="outcome" value="Complete"/>
</form>
</body>
</html>