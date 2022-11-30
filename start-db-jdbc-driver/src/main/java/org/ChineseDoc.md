### 1. AvaticaStatement
在avatica上实现statement（抽象类）

---
* Statement：是 Java 执行数据库操作的一个重要接口，用于在已经建立数据库连接的基础上，向数据库发送要执行的SQL语句。<br/><br/>
* PreparedStatement：继承自Statement,可以使用占位符，是预编译的，批处理比Statement效率高。<br/><br/>
* CallableStatement：继承自Statement，可以接收过程的返回值，主要用于调用数据库中的存储过程。<br/><br/>
* handle : 句柄 (statement对象的id)。<br/><br/>
* checkOpen() : 检查statement是否开启。<br/><br/>
* executeInternal() : 执行一句sql。<br/><br/>
* executeBatchInternal() : 执行批量的sql。<br/><br/>
* executeQuery() : 执行查询语句。<br/><br/>
### 2. Driver
自定义JDBC驱动

---
* static {
  new Driver().register();
  } : 注册当前驱动（默认执行操作）。<br/><br/>
* createDriverVersion() : 载入驱动信息。<br/><br/>
* connect() : 创建一个connection。<br/><br/>