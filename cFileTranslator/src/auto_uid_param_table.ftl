

#include "types.h"

// ${md5}

const U08 * const logger_hash = "${hash}";
const U08 * const git_hash = "${git_hash}";

<#list uid_massive as m>
/*uid = ${m_index}*/const  U08 ${m};
</#list>




const U08 * const uid_index_tbl[] =
{
<#list uid_index as c>
/*uid = ${c_index}*/    ${c},
</#list>
};  



const U08 const uid_param_tbl[] =
{
<#list uid_param as c>
/*uid = ${c_index}*/    ${c},
</#list>
};  

 


