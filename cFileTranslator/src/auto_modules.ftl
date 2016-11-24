

#ifndef __LOG_MODULES_H__ 
#define __LOG_MODULES_H__


#include "common.h"

// ${md5}
       
typedef struct
{
<#list modules as c>	
/*module = ${c_index}*/      U32   ${c}:1;
</#list>
} log_modules_t;



#endif
