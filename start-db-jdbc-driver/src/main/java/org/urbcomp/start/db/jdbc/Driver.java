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

package org.urbcomp.start.db.jdbc;

import org.apache.calcite.avatica.AvaticaConnection;
import org.apache.calcite.avatica.ConnectStringParser;
import org.apache.calcite.avatica.DriverVersion;
import org.apache.calcite.avatica.remote.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/** 自定义JDBC驱动
 * custom JDBC driver
 *
 * @author jimo
 **/
public class Driver extends org.apache.calcite.avatica.remote.Driver {
    //  默认注册当前驱动
    static {
        new Driver().register();
    }

    //  返回url前缀： "jdbc:start-db:"
    @Override
    protected String getConnectStringPrefix() {
        return "jdbc:start-db:";
    }

    //  编写并返回驱动信息
    @Override
    protected DriverVersion createDriverVersion() {
        return DriverVersion.load(
            org.apache.calcite.avatica.remote.Driver.class,
            "org-apache-calcite-jdbc.properties",
            "Start-DB Remote JDBC Driver",
            "1.0.0-SNAPSHOT",
            "MYSQL",
            "5.1.47"
            // "START-DB",
            // "1.0.0-SNAPSHOT"
        );
    }

    //  返回一个connection Connection：建立与数据库的连接，可以创建数据库连接对象statement或preparedstatement
    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }
        final String prefix = getConnectStringPrefix();
        assert url.startsWith(prefix);
        final String urlSuffix = url.substring(prefix.length());
        final Properties info2 = ConnectStringParser.parse(urlSuffix, info);
        final AvaticaConnection conn = factory.newConnection(this, factory, url, info2);
        handler.onConnectionInit(conn);
        if (conn == null) {
            // It's not an url for our driver
            return null;
        }

        Service service = conn.getService();

        // super.connect(...) should be creating a service and setting it in the AvaticaConnection
        assert null != service;

        service.apply(
            new Service.OpenConnectionRequest(
                conn.id,
                Service.OpenConnectionRequest.serializeProperties(info2)
            )
        );
        return conn;
    }
}
