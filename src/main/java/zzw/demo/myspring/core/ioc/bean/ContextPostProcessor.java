package zzw.demo.myspring.core.ioc.bean;

import zzw.demo.myspring.core.AnnotationApplicationContext;

/**
 * IOC容器创建前后调用
 */
public interface ContextPostProcessor {
    void beforeContextInitialization(AnnotationApplicationContext context);

    void afterContextInitialization(AnnotationApplicationContext context);
}
