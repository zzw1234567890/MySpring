package zzw.demo.myspring.core.ioc.bean;

/**
 * 提供自定义初始化，在属性注入之后调用
 */
public interface InitializingBean {
    void afterPropertiesSet();
}
