### 1. RexImpTable
实现行表达式中的各种操作符operator

---
* RexNode：（Rex：Row-level expression）行表达式（标量表达式），蕴含的是对一行数据的处理逻辑。常见的行表达式包括RexLiteral(常量表达式)，，RexCall(一个操作符和零或多个表达式作为操作数组成的表达式) 等。例如一句SQL： time  <  20。
  其中20为RexLiteral，time<20 和 < 为rexcall。<br/><br/>
### 2. RexToLixTranslator
将行表达式rexnode 转换成linq4j表达式

---
* linq4j：一个针对JAVA的linq移植版本，LINQ代表语言集成查询，它允许我们用SQL查询数据库的方式来查询数据的集合。
  Calcite 会将 sql 生成的 linq4j 表达式生成可执行的 Java 代码。<br/><br/>
### 3. CalciteConnectionImpl
在calcite上实现connection（抽象类，允许新版本的jdbc增添东西）

---
* Connection ：建立与数据库的连接，可以创建各种类型的statement。<br/><br/>
### 4. JavaTypeFactoryImpl
实现java类型的工厂方法

---
* Factory：工厂模式，将创建对象的具体过程屏蔽隔离起来，从而达到更高的灵活性。在面向对象编程中，创建对象实例最常用的方式就是通过 new 操作符构造一个对象实例，但在某些情况下，new 操作符直接生成对象会存在一些问题。举例来说，对象的创建需要一系列的步骤：可能需要计算或取得对象的初始位置、选择生成哪个子对象实例、或在生成之前必须先生成一些辅助对象。 在这些情况，新对象的建立就是一个 “过程”，而不仅仅是一个操作，就像一部大机器中的一个齿轮传动。针对上面这种情况，我们如何轻松方便地构造对象实例，而不必关心构造对象示例的细节和复杂过程？解决方案就是使用一个工厂类来创建对象。<br/><br/>
### 5. CalciteCatalogReader
实现目录读取器,用于在语法检查过程中访问元数据,包含了对catalog中对象的各种操作方法

---
* Calcite的Catalog 结构复杂，但我们可以从这个角度来理解Catalog，它是 Calcite 在不同粒度上对元数据所做的不同级别的抽象。首先最细粒度的是 RelDataTypeField，代表某个字段的名字和类型信息，多个RelDataTypeField 组成了一个RelDataType，表示某行或某个标量表达式的结果的类型信息。再之后是一个完整表的元数据信息，即Table。最后我们需要把这些元数据组织存储起来进行管理，于是就有了Schema。<br/><br/>
### 6. RelDataTypeFactoryImpl
实现reldata类型的抽象工厂模式，定义了各种方法以实例化 SQL、Java、集合类型，创建这些类型都实现了 RelDataType 接口。

---
* relnode： relational expression，代表了对数据的一个处理操作，常见的操作有 Sort、Join、Project、Filter、Scan 等。它蕴含的是对整个 Relation 的操作，而不是对具体数据的处理逻辑。<br/><br/>
### 7. RexBuilder
实现rexbuilder（rexnode的工厂方法）<br/><br/>
### 8. RexLiteral
实现rexliteral，通过rexbuilder生成，包含常量的值和类型，值可以通过getvalue方法获取，类型对外隐藏，值和类型必须对应。<br/><br/>
### 9. CalciteResource
sql语法检查，通过抛出异常来禁用掉一些sql语法。<br/><br/>
### 10. ScalarFunctionImpl
实现标量函数。

---
* ScalarFunction ：标量函数，类似于Map，可以把 0 个、 1 个或多个标量值转换成一个标量值，输入是一行数据中的字段，输出则是唯一的值。<br/><br/>
* method : 相当于哈希函数，求值方法。<br/><br/>
* functions() : 返回一个map<method名字,根据method构造的scalar函数或table函数>。<br/><br/>
* getFunctionName() : 返回method名。<br/><br/>
* create() : 构建一个标量函数基于传入的求值方法。<br/><br/>
### 11. SqlDropSchema
实现删除schema操作的语法分析树。<br/><br/>
### 12. SqlDropTable
删除table操作的语法分析树。<br/><br/>
### 13. SqlStdOperatorTable
包含sql标准操作符和函数的实现的表。<br/><br/>
### 14. JavaToSqlTypeConversionRules
java类型转化为sql类型的规则。<br/><br/>
### 15. SqlTypeAssignmentRule
判断两个类型继承关系的规则，A.isAssignableFrom(B)：类(B)继承于另一个父类(A)，或者两个类相同。<br/><br/>
### 16. SqlTypeFamily
sql类型的分类方法。<br/><br/>
### 17. SqlTypeName
sql类型名的枚举类，包含一些get信息的方法。<br/><br/>
### 18. SqlTypeUtil
包含对sql类型的操作方法（用在sql验证和类型溯源过程中）。<br/><br/>
### 19. SqlOperatorTables
创建包含空间或混合sql操作符的表。<br/><br/>
### 20. SqlAsOperator
实现as操作符：关联一个别名。<br/><br/>
### 21. SqlDialect
实现sql方言，概述不同sql之间的差异，包含一些处理方法。<br/><br/>
### 22. SqlJdbcFunctionCall
实现语法分析树中代表JDBC函数调用的结点。<br/><br/>
### 23. SqlUtil
sql方法(抽象类)，包含sql解析用到的一些静态方法。<br/><br/>
### 24. SqlToRelConverter
实现语义分析器，将sqlnode转化为relnode。<br/><br/>
