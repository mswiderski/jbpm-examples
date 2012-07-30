<html>
<body>
<h2>first</h2>
<hr>
<#if task.descriptions[0]??>
Description: ${task.descriptions[0].text}<BR/>
</#if>
<form action="complete" method="POST" enctype="multipart/form-data">
Zip code: <input type="text" name="on1" /><BR/>
Zip code: <input type="text" name="on2" /><BR/>
Zip code: <input type="text" name="on3" /><BR/>
Zip code: <input type="text" name="on4" /><BR/>
Zip code: <input type="text" name="on5" /><BR/>
<BR/>
<input type="submit" name="outcome" value="Complete"/>
</form>
</body>
</html>