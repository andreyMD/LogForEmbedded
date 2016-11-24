
#ifndef __LOG_${ifdef}_H__ 
#define __LOG_${ifdef}_H__



// ${md5}



typedef enum
{
<#list uid as c>	
         ${c},  
</#list>                                                  
} log_uid_${ifdef}t;


#endif 
