简介：根据自己对spring的理解，实现了Spring IOC和Spring AOP的部分功能
## IOC容器
> 容器创建过程：初始化、扫描bean、解析配置类、创建单例bean

核心功能:
- 使用注解+Java类进行配置
- 实现了自动扫描bean，并且支持指定包扫描
- 实现单例bean和多例bean，支持懒加载
- 使用反射实现创建bean和属性自动注入
- 使用二级缓存解决循环依赖
- 设计模式上使用了单例模式和工厂模式

扩展功能:
- 为创建bean提供BeanNameAware接口，可以保存beanName到属性中
- 为创建bean提供BeanInitialization接口，在bean初始化之后调用
- 提供BeanPostProcessor接口，before和after两个方法，在bean初始化前后调用
- 提供ContextPostProcessor接口，before和after两个方法，在创建单例bean容器前后执行
- 提供BeanConfiguration接口，在容器准备阶段调用配置类的config方法

## AOP
核心功能:
- 依赖IOC容器
- 使用@Aspect注解自定义切面
- 使用@PointCut注解定义切入点
- 提供@around，@before，@after，@afterReturning，@afterThrowing五种通知类型，并且可以自定义调用顺序
- 使用cglib动态代理和反射创建代理对象
- 使用责任链设计模式创建通知执行器链

扩展功能:
- 使用模式方法设计模式，提供AdviseExecutor抽象类，支持自定义通知类型以及运行逻辑