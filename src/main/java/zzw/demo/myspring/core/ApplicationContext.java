package zzw.demo.myspring.core;

public interface ApplicationContext {
    Object getBean(String beanName);
    <T> T getBean(Class<T> clazz);
}
