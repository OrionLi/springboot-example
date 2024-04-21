# Springboot 项目开发示例

## 建库建表

MySQL 链接地址是实验室的，不在实验室的话就用我给的 SQL 文件，然后改下 MySQL url

排序规则使用 `utf8mb4_0900_ai_ci` ,虽然 MySQL 默认是 `UTF-8` 编码, 但是是 `utf8mb3`, 并不是完整的

不详细解释，详细资料看这: [MySQL常用排序规则](https://blog.csdn.net/munangs/article/details/126617226)

```mysql
CREATE TABLE `user`
(
    `id`               bigint       NOT NULL,
    `username`         varchar(32)  NOT NULL,
    `password`         varchar(255) NOT NULL,
    `phone_number`     varchar(32)           DEFAULT NULL COMMENT '电话号码',
    `sex`              char(1)               DEFAULT NULL COMMENT '性别',
    `register_date`    datetime(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `last_modify_date` datetime(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (`id`),
    UNIQUE KEY `user_username_uindex` (`username`),
    CHECK (sex = 'M' OR sex = 'F')
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
```

几个细节说一下

### id 选择

千万别用 UUID 做主键！！！UUID 是无序的，索引插入极慢。

可以使用 BIGINT 的自增类型作为主键，同时由于整型的自增性，数据库插入也是顺序的，性能较好。

但你要注意，使用 BIGINT 的自增类型作为主键的设计仅仅适合**非核心业务表**，比如告警表、日志表等。**真正的核心业务表，一定不要用自增键做主键
**，主要有 6 个原因：

- 自增存在回溯问题；
- 自增值在服务器端产生，存在并发性能问题；
- 自增值做主键，只能在当前实例中保证唯一，**不能保证全局唯一**；
-

公开数据值，容易引发安全问题，例如知道地址 [http://www.example.com/User/10/](http://www.example.com/customers/10/?fileGuid=xxQTRXtVcqtHK6j8)
，很容猜出 User 有 11、12 依次类推的值，容易引发数据泄露；

- MGR（MySQL Group Replication） 可能引起的性能问题；
- 分布式架构设计问题。

嗯，我用的 [IdGenerator](https://github.com/yitter/IdGenerator), 递增的魔改雪花算法，感觉还行。详情看 `User` 类

### 密码设计

在数据库表结构设计时，**千万不要直接在数据库表中直接存储密码**。

如果使用固定盐值的加密算法，存在三个主要问题：

- 若 salt 值被（离职）员工泄漏，则外部黑客依然存在暴利破解的可能性；
- 对于相同密码，其密码存储值相同，一旦一个用户密码泄漏，其他相同密码的用户的密码也将被泄漏；
- 固定使用 MD5 加密算法，一旦 MD5 算法被破解，则影响很大。

所以一个真正好的密码存储设计，应该是：**动态盐 + 非固定加密算法**。

推荐这么设计密码，列 password 存储的格式如下：

```
$salt$cryption_algorithm$value
```

其中：

- $salt：表示动态盐，每次用户注册时业务产生不同的盐值，并存储在数据库中。若做得再精细一点，可以动态盐值 + 用户注册日期合并为一个更为动态的盐值。
- $cryption_algorithm：表示加密的算法，**如 `v1` 表示 `MD5` 加密算法，`v2` 表示 `AES256` 加密算法，`v3` 表示 `AES512` 加密算法等
  **。
- $value：表示加密后的字符串。

自己拿着 `123456`
当密码测就知道了。另外，我的实现用了策略模式，感兴趣的看我博客：[https://orionli.github.io/2023/07/07/design-patterns/](https://orionli.github.io/2023/07/07/design-patterns/)

常用的就模版方法模式、策略模式、责任链模式、适配器模式、观察者模式

### 枚举字段设计

设计表结构时，会遇到一些固定选项值的字段。例如，性别字段（`sex`），只有男或女；又或者状态字段（`state`），有效的值为运行、停止、重启等有限状态。

大多数开发人员喜欢用 INT 的数字类型去存储性别字段，比如：

```sql
CREATE TABLE `User`
(
    `id`  bigint NOT NULL AUTO_INCREMENT,
    `sex` tinyint DEFAULT NULL,
    # 略......

    PRIMARY KEY (`id`)
) ENGINE = InnoDB；
```

其中，tinyint 列 sex 表示用户性别，但这样设计问题比较明显。

- **表达不清**：在具体存储时，0 表示女，还是 1 表示女呢？每个业务可能有不同的潜规则；
- **脏数据**：因为是 tinyint，因此除了 0 和 1，用户完全可以插入 2、3、4 这样的数值，最终表中存在无效数据的可能，后期再进行清理，代价就非常大了。

在 MySQL 8.0 版本之前，可以使用 ENUM 字符串枚举类型，只允许有限的定义值插入。由于**类型 ENUM 并非 SQL 标准的数据类型**，而是
MySQL 所独有的一种字符串类型。**抛出的错误提示也并不直观**，这样的实现总有一些遗憾，主要是因为 MySQL 8.0 之前的版本并没有提供约束功能。

自 MySQL 8.0.16 版本开始，数据库原生提供 [CHECK 约束功能](https://www.sjkjc.com/mysql/check-constraint/)
，可以方便地进行有限状态列类型的设计

看我的 sql 就能看出来，我使用了 `CHECK (sex = 'M' OR sex = 'F')` 进行约束

### 日期类型选择

MySQL 日期类型有 DATETIME 和 TIMESTAMP，另外，也可以用 INT 存时间，比如 `20240101123435` 表示 2024-01-01 12:34:35
（毫秒值懒得打了，意会吧）

- 当前每个 CPU 每秒可执行上亿次的计算，INT 转 TIMESTAMP 性能不是问题。但在后期运维和数据分析时，**使用 INT 存储日期，是会让
  DBA 和数据分析人员发疯的**，INT 的可运维性太差。
- **现在距离 TIMESTAMP 的最大值‘2038-01-19 03:14:07’已经很近**，若要将时间精确到毫秒，TIMESTAMP 要 7 个字节，和 DATETIME 8
  字节差不太多，需要仔细考虑。
- **DATETIME 不存在时区转化问题，对于时区问题，可以由前端或者服务这里做一次转化，不一定非要在数据库中解决。**

我建议的**表结构设计规范：每条记录都要有一个时间字段**

在做表结构设计规范时，**强烈建议每张业务核心表都增加一个 DATETIME 类型的 `last_modify_date` 字段，并设置修改自动更新机制**
，即标识每条记录最后修改的时间。

通过字段 `last_modify_date` 定义的 `ON UPDATE CURRENT_TIMESTAMP(6)`，那么每次这条记录，则都会自动更新 `last_modify_date`
为当前时间。

> `CURRENT_TIMESTAMP(6)` 意思是当前时间，精确到小数点后六位

**这样设计的好处是**：用户可以知道每个用户最近一次记录更新的时间，以便做后续的处理。比如在电商的订单表中，可以方便对支付超时的订单做处理；在金融业务中，可以根据用户资金最后的修改时间做相应的资金核对等。

## Dao 层和 Service 层

虽然我是用的 MybatisX 生成的代码，但你可以看到我把他改成了 `UserDao` ，是的，我想更强调它的职责：一个只负责和自己对应表交互的东西

虽然 Mybatis Plus 很好用，但请不要完全依赖 Service，而碰都不碰 Dao

Service 可以用来校验数据合法，用来调用多个 Mapper，聚合处理...... 但不要让它身兼 Dao 的工作

这样你的 Service 层和 Dao 层是强耦合的，做个 SQL 优化，可能还牵扯到原本的业务逻辑；业务逻辑变更，没准还要频繁改动 SQL
语句...... 不要这样！！！

真正好的分层，Dao 层变动是不影响 Service 层调用的。Service 就只调 Dao，而不会去调另一个 Service。否则耦合的东西多了，牵一发而动全身

要明确的是：Dao 层是不经常改动的，因为他是偏底层的，一般来说大把 Service 要调它。**底层的东西，不要经常动！！！否则等着被别人线下真人快打吧
**

建议看看这里：[正确使用MP](https://www.zhihu.com/answer/3282783134)

嗯，因为改名叫 `UserDao` 了，`application.yml` 的 `mybatis-plus.mapper-locations` 要记得改一下

## Service 层和 Controller 层

显而易见，我并没有直接用 `domain` 包里面的类来接收参数, 而是用的 `VO` 类。两个原因:

- 让 domain 专职与和 Dao 交互，非空校验啥的让 VO 去做。而且通过 `springboot-validation` 和 `@NotNull` (给包装类用)
  和 `@NotEmpty` (
  给字符串用)，校验非常轻松，完全不用自己写 if-else。甚至注解的内容都可以让 ai 代码助手写
- 同样是打开京东，受限于屏幕尺寸，手机 APP 和电脑网页能展示和接收的内容是不一样的。如果用 `domain`
  的类通吃，还要写一堆注解告诉前端那些要传，哪些不要传。但如果用 VO，那前端写的也快乐（需要啥参数传啥），后端维护也快乐（不用写大堆备注给前端了）

另外，MapStruct 让 VO 转换 domain
非常轻松，详细入门看这 [OrionLi 翻译的 MapStruct 文档](https://github.com/OrionLi/spring-boot-demo/blob/master/demo-mapping-mapstruct/README.md)

> 这里说下`@NotEmpty`、`@NotBlank`、`@NotNull` 的区别：
> 1. `@NotNull`
     >
     >   适用于基本数据类型(`Integer`，`Long`，`Double`，`Date等等`)，当 `@NotNull` 注解被使用在 `String` 类型的数据上，则表示该数据不能为
     `null`（但是可以为空，如`""`、`''`）
>
> 2. `@NotBlank`
     >
     >   适用于 `String` 类型的数据上，加了 `@NotBlank` 注解的参数不能为 `null` 且 `trim()` 之后 `size > 0`
>
> 3. `@NotEmpty`
     >
     >   适用于 `String`、`Collection集合`、`Map`、数组等等，加了`@NotEmpty` 注解的参数不能为 `null` 或者 长度为 0

## 异常处理

自己看代码，懒得讲

## 接口文档

设置成 dev 环境才启用，默认就是。系统启动完会打印访问地址的。也可以去启动类看