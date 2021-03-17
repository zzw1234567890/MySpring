package zzw.demo.myspring.core.ioc.bean;

/**
 * 在bean初始化前后调用对应的方法
 */
public interface BeanPostProcessor {
    default Object postProcessBeforeInitialization(Object bean, String beanName){
        return bean;
    }
    default Object postProcessAfterInitialization(Object bean, String beanName){
        return bean;
    }
}
