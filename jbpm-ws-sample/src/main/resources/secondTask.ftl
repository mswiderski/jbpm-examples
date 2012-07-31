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
Requested weather forecast for selected zip codes:
<#list output as entry>
========================================<BR/>
   ${entry.state}<BR/>
   ${entry.city}<BR/>
   ${entry.description}<BR/>
   ${entry.temperature}<BR/>
   ${entry.relativeHumidity}<BR/>
   ${entry.wind}<BR/>
   ${entry.pressure}<BR/>
   ${entry.visibility}<BR/>
   ${entry.windChill}<BR/>
   ${entry.remarks}<BR/>
  ========================================
</#list>

<form action="complete" method="POST" enctype="multipart/form-data">
<BR/>
<input type="submit" name="outcome" value="Complete"/>
</form>
</body>
</html>