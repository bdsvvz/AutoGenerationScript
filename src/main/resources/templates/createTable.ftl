<#--建表结构模板-->
/****** Object:  Table [dbo].[${table_name}]    Script Date: ${.now?string("yyyy-MM-dd HH:mm:ss")} ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[${table_name}]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[${table_name}](
<#list tableColumnInfo as map>
    [${map['name']}] [${map['datatype']}]<#if map['datatype'] == "varchar" || map['datatype'] == "nvarchar">(${map['length']})<#elseif map['datatype'] == "numeric">(${map['numericprecision']},${map['numericscale']})<#else></#if>  ${map['nullable']?string('NULL,','NOT NULL,')}
</#list>
CONSTRAINT [${primaryKeyInfo['name']}] PRIMARY KEY CLUSTERED
(
    [${primaryKeyInfo['column_name']}] ASC
)WITH (PAD_INDEX = ${primaryKeyInfo['is_padded']?string('ON','OFF')}, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = ${primaryKeyInfo['ignore_dup_key']?string('ON','OFF')}, ALLOW_ROW_LOCKS = ${primaryKeyInfo['allow_row_locks']?string('ON','OFF')}, ALLOW_PAGE_LOCKS = ${primaryKeyInfo['allow_page_locks']?string('ON','OFF')},FILLFACTOR = ${primaryKeyInfo['fill_factor']}) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO


<#--可重复建表字段模板-->
<#list tableColumnInfo as map>
/****** Object:  Column [${map['name']}]    Script Date: ${.now?string("yyyy-MM-dd HH:mm:ss")} ******/
IF COL_LENGTH('${table_name}', '${map['name']}') IS NULL
BEGIN
    ALTER TABLE [${table_name}] ADD [${map['name']}] [${map['datatype']}]<#if map['datatype'] == "varchar" || map['datatype'] == "nvarchar">(${map['length']})<#elseif map['datatype'] == "numeric">(${map['numericprecision']},${map['numericscale']})<#else></#if>  ${map['nullable']?string('NULL','NOT NULL')};
END
</#list>



<#--建表索引模板-->
<#list nonClusteredList as item>
/****** Object:  Index [${item['name']}]    Script Date: ${.now?string("yyyy-MM-dd HH:mm:ss")} ******/
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[${table_name}]') AND name = N'${item['name']}')
CREATE NONCLUSTERED INDEX [${item['name']}] ON [dbo].[${table_name}]
(
<#list item['list2'] as child>
    <#if child?is_last>
        ${child['column_name']} ${child['is_descending_key']?string('DESC','ASC')}
    <#else>
        ${child['column_name']} ${child['is_descending_key']?string('DESC,','ASC,')}
    </#if>
</#list>
)
<#if  item['includeCount'] gt 0 >
INCLUDE (
<#list item['list1'] as child>
    <#if child?is_last>
        ${child['column_name']}
    <#else>
        ${child['column_name']} ,
    </#if>
</#list>
)
</#if>
WITH (PAD_INDEX = ${item['is_padded']?string('ON','OFF')}, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ${item['allow_row_locks']?string('ON','OFF')}, ALLOW_PAGE_LOCKS = ${item['allow_page_locks']?string('ON','OFF')} <#if item.fill_factor gt 0>, FILLFACTOR = ${item.fill_factor}</#if>) ON [PRIMARY]
GO
SET ANSI_PADDING ON

GO
</#list>

<#--字段设置默认值-->
<#list defaultColumnList as item>
IF NOT EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[dbo].[${item['name']}]') AND type = 'D')
BEGIN
    ALTER TABLE [dbo].[${table_name}] ADD  CONSTRAINT [${item['name']}]  DEFAULT ${item['text']} FOR [${item['column_name']}]
END

GO
</#list>