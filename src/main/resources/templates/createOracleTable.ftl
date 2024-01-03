/****** Object:  Table [dbo].[${table_name?upper_case}]    Script Date: ${.now?string("yyyy-MM-dd HH:mm:ss")} ******/
DECLARE FLAG_NUM NUMBER;
BEGIN
SELECT
  COUNT(*) INTO FLAG_NUM
FROM
  USER_TABLES ATS
WHERE
  ATS.TABLE_NAME = UPPER('${table_name?upper_case}');
IF FLAG_NUM = 0 THEN EXECUTE IMMEDIATE '${data}';
END IF;
END;
/

<#macro greet umap>
    <#if umap['data_type'] == "CHAR" || umap['data_type'] == "RAW" || umap['data_type'] == "NVARCHAR2" ||  umap['data_type'] == "VARCHAR2">
        <#if umap['data_length'] gt 2000 && umap['data_type'] == "NVARCHAR2">
            (2000)
        <#else>
            (${umap['data_length']})
        </#if>
    <#elseif umap['data_type'] == "NUMBER">
        <#if umap['data_precision']?has_content>
            (${umap['data_precision']}<#if umap['data_scale']?has_content && umap['data_scale'] gt 0>,${umap['data_scale']}</#if>)
        </#if>
    <#else>
    </#if>
</#macro>



<#list tableColumnInfo as map>
/****** Object:  Colummn [${map['column_name']?upper_case}]    Script Date: ${.now?string("yyyy-MM-dd HH:mm:ss")} ******/
DECLARE FLAG_NUM NUMBER;
BEGIN
SELECT
  COUNT(*) INTO FLAG_NUM
FROM
  USER_TABLES ATS
WHERE
  ATS.TABLE_NAME = UPPER('${table_name?upper_case}');
IF FLAG_NUM > 0 THEN
SELECT
  COUNT(*) INTO FLAG_NUM
FROM
  USER_TAB_COLUMNS T
WHERE
  T.TABLE_NAME = UPPER('${table_name?upper_case}')
  AND T.COLUMN_NAME = UPPER('${map['column_name']}');
IF FLAG_NUM = 0 THEN EXECUTE IMMEDIATE '
    ALTER TABLE ${table_name?upper_case} ADD ${map['column_name']} ${map['data_type']}<#compress><@greet umap=map /></#compress> ${(map['nullable'] == 'N')?then('NOT NULL','NULL')} ${(map['data_default']?has_content)?then('default '+map['data_default'],'')}';
END IF;
END IF;
END;
/
</#list>

<#list tableIndexInfo as map>
DECLARE FLAG_NUM NUMBER;
BEGIN
SELECT
  COUNT(*) INTO FLAG_NUM
FROM
  user_ind_columns
WHERE
  TABLE_NAME = UPPER('${table_name}')
  AND INDEX_NAME = '${map['index_name']}';
IF FLAG_NUM < 1 THEN
SELECT
  COUNT(*) INTO FLAG_NUM
FROM
  user_ind_columns
WHERE
  TABLE_NAME = UPPER('${table_name}')
  AND COLUMN_NAME = UPPER('${map['column_name']}');
IF FLAG_NUM < 1 THEN
    EXECUTE IMMEDIATE
    <#if map['uniqueness'] == 'NONUNIQUE'>
        'CREATE INDEX ${map['index_name']} on ${table_name}(${map['column_name']} ${map['descend']})';
    <#else>
        'ALTER TABLE ${table_name}  ADD CONSTRAINT ${map['index_name']} PRIMARY KEY (${map['column_name']});';
    </#if>
END IF;
END IF;
END;
/
</#list>

