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

import org.junit.Test;
import org.junit.Ignore;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.urbcomp.start.db.test.GetData.getResultArray;
import static org.urbcomp.start.db.test.GetCasePathByXML.getSqlCaseXMLs;
import static org.urbcomp.start.db.test.RunSingleSQLCase.runSingleCase;
import static org.urbcomp.start.db.test.AutoWriteExpect.writeExpect;
import static org.urbcomp.start.db.test.RunSingleSQLCase.getConnect;

public class MainTest {
    private static final Logger log = LoggerFactory.getLogger(MainTest.class);

    @Test
    @Ignore
    public void testAutoWriteExpect() throws Exception {
        String xmlPath = Objects.requireNonNull(
            MainTest.class.getClassLoader().getResource("cases/ddl/database.xml")
        ).getPath();
        writeExpect(xmlPath);

    }

    @Test
    @Ignore
    public void testUpdate() {
        try (Connection conn = getConnect(); Statement stmt = conn.createStatement()) {
            String sql = "create table int_table (int1 Integer)";
            int rowCount = stmt.executeUpdate(sql);
            log.info("影响的行数为:" + rowCount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Ignore
    public void testQuery() throws Exception {
        try (
            Connection conn = getConnect();
            Statement stat = conn.createStatement();
            ResultSet result = stat.executeQuery("select * from t_test order by idx;")
        ) {
            ArrayList<String> resultArray = getResultArray(result);
            for (String s : resultArray) {
                log.info(s);
            }
        }
    }

    @Test
    @Ignore
    public void singleSQLCaseTest() throws Exception {
        // 执行单个xml测试用例文件
        String xmlResource = Objects.requireNonNull(
            RunSingleSQLCase.class.getClassLoader().getResource("cases/ddl/database.xml")
        ).getPath();
        log.info("xmlResource:" + xmlResource);
        runSingleCase(xmlResource);
    }

    @Test
    @Ignore
    public void allSQLCaseTest() throws Exception {
        // 执行所有测试用例文件
        ArrayList<String> sqlCaseXMLs = getSqlCaseXMLs();
        for (String sqlCaseXML : sqlCaseXMLs) {
            log.info("执行文件:" + sqlCaseXML);
            runSingleCase(sqlCaseXML);
        }
    }

}
