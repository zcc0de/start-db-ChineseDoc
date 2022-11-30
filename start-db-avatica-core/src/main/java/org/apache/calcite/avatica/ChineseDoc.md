## 1. AbstractCursor
用于resultset的抽象游标类

---
* ResultSet ：对数据库执行查询操作返回的结果集,ResultSet resultSet = statement.executeQuery(sql)。<br/><br/>
* wasNull() : 判断游标所指行是否为空。<br/><br/>
* Accessor：数据访问器。<br/><br/>
* createAccessor(
  ColumnMetaData columnMetaData,
  Getter getter,
  Calendar localCalendar,
  ArrayImpl.Factory factory
  ) : 根据元数据提供的数据类型创建相应类型的数据访问器。<br/><br/>
* Getter ：get方法，从当前游标所指行的特定列中get一个值。<br/><br/>
* next() :移动游标至下一行。<br/><br/>
* get……() ： 将getter获取的数据转换为指定数据类型，由相应的accessor实现不同类型的get……()。<br/><br/>
## 2. ColumnMetaData
数据表的元数据类

---
* FieldDescriptor : 字段描述符。<br/><br/>
* hascode() : 重写hashcode方法，返回元数据类各成员的哈希码值。<br/><br/>
* equals() : 重写equals方法，传入一个columnmetadata，判断两者各成员是否相同。<br/><br/>
* dummy() : 创建一个成员都为空的columnmetadata，除了传入的指定字段。<br/><br/>
* rep() : 一个枚举类，用来对应数据库和java中的数据类型，其中有一些本项目特有的数据类型，例如point。<br/><br/>
* jdbcget() : 获取查询结果中指定列的值，根据其数据类型调用相应的get……()方法。<br/><br/>
* nonPrimitiveRepOf() : 传入sql类型，返回相应类型的rep，如果是基本数据类型，返回等价类型的rep。<br/><br/>
* AvaticaType : 描述数据库表的一列，包含三个属性，id，name和rep，是ScalarType，StructType和Array type的父类。<br/><br/>