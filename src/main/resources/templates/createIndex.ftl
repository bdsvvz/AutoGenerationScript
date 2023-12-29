<#macro greet umap>
<#if umap['datatype'] == "varchar" || umap['datatype'] == "nvarchar" || umap['datatype'] == "nvarchar" || umap['datatype'] == "varbinary" || umap['datatype'] == "binary" ||  umap['datatype'] == "nchar" ||  umap['datatype'] == "char"><#if umap['length'] gt 0>(${umap['length']})<#else>('max')</#if><#elseif umap['datatype'] == "datetime2">(${umap['numericscale']})<#elseif umap['datatype'] == "numeric">(${umap['numericprecision']},${umap['numericscale']})<#else></#if>
</#macro>


<#--可重复建表字段模板-->
<#list tableColumnInfo as map>
    /****** Object:  Column [${map['name']}]    Script Date: ${.now?string("yyyy-MM-dd HH:mm:ss")} ******/
    IF COL_LENGTH('${table_name}', '${map['name']}') IS NULL
    BEGIN
    ALTER TABLE [${table_name}] ADD [${map['name']}] [${map['datatype']}]<@greet umap=map></@greet> ${map['nullable']?string(' NULL',' NOT NULL')};
    END
</#list>