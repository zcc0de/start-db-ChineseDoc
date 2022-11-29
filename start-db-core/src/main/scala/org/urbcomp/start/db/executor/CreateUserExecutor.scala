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

package org.urbcomp.start.db.executor

import org.urbcomp.start.db.infra.{BaseExecutor, MetadataResult}
import org.urbcomp.start.db.metadata.MetadataAccessUtil
import org.urbcomp.start.db.metadata.entity.{Database, User}
import org.urbcomp.start.db.parser.dcl.SqlCreateUser

case class CreateUserExecutor(n: SqlCreateUser) extends BaseExecutor {
  override def execute[Int](): MetadataResult[Int] = {
    val userName = n.getUserName.names.get(0)
    val existed = MetadataAccessUtil.getUser(userName)
    if (existed != null) {
      throw new IllegalArgumentException("User already exist " + userName)
    }
    val password = n.getPassword
    checkPassword(password)
    MetadataAccessUtil.insertUser(new User(-1, userName, password))
    val user = MetadataAccessUtil.getUser(userName)
    MetadataAccessUtil.insertDatabase(new Database(-1, user.getId, "default"))
    MetadataResult.buildDDLResult(2)
  }

  // TODO password spec
  def checkPassword(password: String): Unit = {
    if (password == "") {
      throw new IllegalArgumentException("password must not empty")
    }
    if (password.contains(" ") || password.contains("\t") || password.contains("\n")) {
      throw new IllegalArgumentException("password must not contains empty characters")
    }
  }
}
