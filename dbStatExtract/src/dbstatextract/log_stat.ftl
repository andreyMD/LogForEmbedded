<html>
<body>
<h2>${cur_date}</h2>

<#list imei_msg as m>
<p><h3>${m.imei}</h3><br>
LOG_WARN:${m.warnCnt}<br>
LOG_ERR:${m.errCnt}<br>
${m.errStr}

	
</p>



</#list> 


</body>
</html>