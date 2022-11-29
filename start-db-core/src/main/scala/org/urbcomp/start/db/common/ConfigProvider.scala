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

package org.urbcomp.start.db.common

/**
  * Config Provider
  *
  * @author zaiyuan
  * @since 0.1.0
  */
object ConfigProvider {

  def getGeomesaHbaseParam(catalog: String): Map[String, String] =
    Map(
      ConfigurationConstants.GEOMESA_HBASE_CATALOG -> catalog,
      ConfigurationConstants.GEOMESA_HBASE_ZOOKEEPERS -> getHBaseZookeepers
    )

  def getHBaseZookeepers: String =
    ConfigurationFactory.getInstance.getProperty(ConfigurationConstants.GEOMESA_HBASE_ZOOKEEPERS)
}
