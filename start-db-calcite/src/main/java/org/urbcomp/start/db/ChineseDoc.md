### 1. AbstractCoordTransformer
抽象类，将各种空间对象转换到指定坐标系下。
* BD09 : 百度坐标系。
* GCJ02 ：火星坐标系。
* WGS84 ：地球坐标系。<br/><br/>
### 2. CoordTransformFunction
将各种空间对象转换到指定坐标系下。<br/><br/>
### 3. DataTypeConversionFunction
生成各种空间对象。<br/><br/>
### 4. GeometricOperationFunction
对空间对象的各种运算方法。<br/><br/>
### 5. GeometricRelationFunction
对空间对象之间各种关系的判断方法。<br/><br/>
### 6. GeometricTypeConversionFunction
接收geojson数据生成相应空间对象。<br/><br/>
### 7. MathFunction
一些数学运算方法。<br/><br/>
### 8. RoadFunction
路段/路网的一些操作方法。<br/><br/>
### 9. StartDBFunction
Start DB Function注解，@StartDBFunction(“函数名称”)。<br/><br/>
### 10. StringFunction
一些对字符串进行操作的方法。<br/><br/>
### 11. TimeFunction
一些对时间数据进行操作的方法。<br/><br/>
### 12. TrajectoryFunction
一些对轨迹对象的操作方法。<br/><br/>
### 13. MetadataResult
对于DDL和DML执行结果的元数据。<br/><br/>
### 14. SqlCreateUser
创建数据库用户的sqlcall。<br/><br/>
### 15. SqlCreateDatabase
创建数据库的sqlcall。<br/><br/>
### 16. SqlTruncateTable
删除表的sqlcall。<br/><br/>
### 17. SqlUseDatabase
使用数据库的sqlcall。<br/><br/>
### 18. SqlShowCreate
抽象类，展示已创建表的sqlcall。<br/><br/>
### 19. SqlShowCreateTable
继承自SqlShowCreate，展示已创建的表的sqlcall。<br/><br/>
### 20. SqlShowDatabases
展示数据库的sqlcall。<br/><br/>
### 21. SqlShowStatus
展示数据库状态的sqlcall。<br/><br/>
### 22. SqlShowTables
展示数据库表的sqlcall。<br/><br/>
### 23. StartDBSqlBaseVisitor
访问sql解析生成的语法树（空的实现,通过继承并实现几个方法可以完全实现）。<br/><br/>
### 24. StartDBSqlLexer
实现sql词法分析器。<br/><br/>
### 25. StartDBSqlParser
实现sql语法解析器。<br/><br/>
### 26. StartDBSqlVisitor
访问sql解析生成的语法树(接口)。<br/><br/>
