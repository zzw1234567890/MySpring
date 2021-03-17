package zzw.demo.myspring.core.ioc.bean;

/**
 * 给用户提供的接口，在创建bean之后自动设置beanName
 */
public interface BeanNameAware {
    void setBeanName(String beanName);
}
