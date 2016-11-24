
package ru.starline.parser.uid.tables;


public class UidParamTable {
    

public final static int uidParamTable[][] = {

<#list uid_massive as m>
/*uid = ${m_index}*/ ${m}
</#list>

    };
}







