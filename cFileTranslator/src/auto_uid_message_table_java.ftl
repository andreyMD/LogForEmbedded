
package ru.starline.parser.uid.tables;

public  class UidMessageTable {

    public final static String uidMessageTable[] = {
    <#list message as c>
/*uid = ${c_index}*/         "${c}  
    </#list>
    };
    
}


