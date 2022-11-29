/*
 * Copyright 2022 ST-Lab
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 */

package org.urbcomp.start.db.test;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.urbcomp.start.db.test.RunSingleSQLCase.DBNAME;
import static org.urbcomp.start.db.test.RunSingleSQLCase.START_TIME;

public class GetData {

    private final static Logger log = LoggerFactory.getLogger(GetData.class);

    /**
     * 返回拼接参数后的sql
     *
     * @param initSql sql文本
     * @param parameters 参数字符串
     * @return 返回拼接后的sql
     * */
    public static String getSqlWithParam(String initSql, String parameters) throws Exception {
        // 解析参数信息 先判断内容是否是有效的, 不能为null或空字符串
        // 参数使用[]包括, 解析的时候先去掉两遍的[]中括号, 然后使用 ][ 分隔
        String[] params;
        StringBuilder sql = new StringBuilder();

        parameters = StringUtils.strip(parameters, "[]");
        // 如果参数出现换行, 就会出现空格, 需要替换下, 保证下面能正常分隔参数
        if (parameters.contains("] [")) {
            parameters = parameters.replace("] [", "][");
        }
        params = parameters.split("]\\[");

        // 参数的数量正确的情况下再拼接sql
        int paramCount = appearNumber(initSql, '?');
        if (paramCount != 0 && paramCount == params.length) {
            // 保证拼接前的sql是个空字符串
            sql.setLength(0);
            // 将参数拼接到sql中, 先将sql以 ? 切分, 然后在加上参数拼接为新的sql字符串
            String[] sqlSplitList = initSql.split("\\?");
            for (int i = 0; i < params.length; i++) {
                sql.append(sqlSplitList[i]).append(params[i]);
            }
            sql.append(sqlSplitList[paramCount]);
        } else {
            log.info("sql中的占位符数量为 " + paramCount + ",\n实际参数数量为 " + params.length);
            throw new Exception("参数不匹配, 请检查测试用例");
        }
        return sql.toString();
    }

    /**
     *以字符串数组的格式返回预期数据
     *
     * @param expectData 预期字符串
     * @param filePath sql用例的文件路径
     * @return 预期内容
     * */
    public static ArrayList<String> getExpectDataArray(String expectData, String filePath)
        throws Exception {
        File parentFile = new File(filePath).getParentFile();
        String expectedPath = parentFile.getPath()
            + File.separator
            + "expected"
            + File.separator
            + expectData;
        StringBuilder expectValue = new StringBuilder();
        ArrayList<String> expectedArray = new ArrayList<>();
        // 获取根标签
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(expectedPath);
        Element rootElement = document.getRootElement();
        // 获取表头信息 metadata标签
        List<Element> metaElements = rootElement.elements("metadata");
        for (Element metaElement : metaElements) {
            String filedName = metaElement.attributeValue("name");
            expectValue.append(filedName).append("\t");
        }
        expectedArray.add(expectValue.toString());
        expectValue.setLength(0);
        for (Element metaElement : metaElements) {
            String filedType = metaElement.attributeValue("type");
            expectValue.append(filedType).append("\t");
        }
        expectedArray.add(expectValue.toString());
        expectValue.setLength(0);
        // 获取row标签的内容
        List<Element> rowElements = rootElement.elements("row");
        for (Element rowElement : rowElements) {
            String rowText = rowElement.getText();
            expectedArray.add(rowText);
        }
        return expectedArray;
    }

    /**
     * 获取指定字符串出现的次数
     *
     * @param srcText 源字符串
     * @param findText 要查找的字符串
     * @return 返回字符串出现的次数
     * */
    private static int appearNumber(String srcText, char findText) {
        int count = 0;
        for (int i = 0; i < srcText.length(); i++) {
            if (srcText.charAt(i) == findText) {
                count += 1;
            }
        }
        return count;
    }

    /**
     * 以字符串数组的形式返回执行结果
     *
     * @param result 执行sql返回的result
     * @return 字符串数组
     * */
    public static ArrayList<String> getResultArray(ResultSet result) throws SQLException {
        // 获取返回数据的列数
        int columnCount = result.getMetaData().getColumnCount();
        // 将表头信息和表头的类型拼接为字符串
        StringBuilder resultStr = new StringBuilder();
        ArrayList<String> resultArray = new ArrayList<>();
        // 拼接表头
        for (int i = 1; i <= columnCount; i++) {
            resultStr.append(result.getMetaData().getColumnName(i)).append("\t");
        }
        resultArray.add(resultStr.toString());
        resultStr.setLength(0);
        // 拼接表头的属性
        for (int i = 1; i <= columnCount; i++) {
            String colType = result.getMetaData().getColumnTypeName(i);
            resultStr.append(typeConvert(colType)).append("\t");
        }
        resultArray.add(resultStr.toString());
        resultStr.setLength(0);
        // 拼接返回的数据
        while (result.next()) {
            for (int i = 1; i <= columnCount; i++) {
                resultStr.append(result.getObject(i)).append("\t");
            }
            resultArray.add(resultStr.toString());
            resultStr.setLength(0);
        }
        return resultArray;
    }

    /**
     * 对返回的数据类型进行转换, 便于后续结果比较
     *
     * @param colType 实际返回的数据类型
     * @return 转换后的数据类型
     * */
    private static String typeConvert(String colType) {
        String type = "";
        switch (colType.toLowerCase()) {
            case "varchar":
                type = "string";
                break;
            case "integer":
                type = "int";
                break;
            case "bool":
                type = "boolean";
                break;
            default:
                type = colType.toLowerCase();
        }
        return type;
    }

    /**
     * 替换文本内容
     *
     * @param sql 需要替换的sql
     * @return 替换后的sql
     * */
    public static String dataTransform(String sql) {
        if (sql.contains("{dbname}")) {
            sql = sql.replace("{dbname}", DBNAME);
        }
        if (sql.contains("{timestamp}")) {
            sql = sql.replace(
                "{timestamp}",
                new SimpleDateFormat("yyMMdd_HHmmss").format(START_TIME)
            );
        }
        if (sql.contains("{dataTime}")) {
            LocalDate date = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            sql = sql.replace("{current_date}", date.format(formatter));
        }
        if (sql.contains("{current_time}")) {
            LocalTime now = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            sql = sql.replace("{current_time}", now.format(formatter));
        }
        if (sql.contains("{current_timestamp}")) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:s");
            sql = sql.replace("{current_timestamp}", formatter.format(new Date()));
        }
        return sql;
    }

}
