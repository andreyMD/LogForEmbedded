

#include "common.h"

// ${md5}
                 
const U16 log_err_uid[] = 
{
<#list log_err_cnt as c>	
      ${c} ,
</#list>
};

U16 log_err_uid_qty = sizeof(log_err_uid)/sizeof(U16); 

U16 log_err_cnt[sizeof(log_err_uid)/sizeof(U16)];


        

