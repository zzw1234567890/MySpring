package zzw.demo.myspring.core.aop.base;

import net.sf.cglib.proxy.Enhancer;
import zzw.demo.myspring.core.AnnotationApplicationContext;
import zzw.demo.myspring.core.ioc.annotation.Autowired;
import zzw.demo.myspring.core.ioc.annotation.Configuration;
import zzw.demo.myspring.core.ioc.bean.ContextPostProcessor;

import java.lang.reflect.Field;
import java.util.Map;

@Configuration
public class AopContextPostProcessor implements ContextPostProcessor {
    @Override
    public void beforeContextInitialization(AnnotationApplicationContext context) {

    }

    /**
     * 为cglib动态代理生成的对象进行属性注入
     * @param context
     */
    @Override
    public void afterContextInitialization(AnnotationApplicationContext context) {
        Map<String, Object> singletonObjects = context.getSingletonObjects();
        for (String beanName : singletonObjects.keySet()) {
            Object bean = singletonObjects.get(beanName);
            Class<?> clazz = bean.getClass();
            if (Enhancer.isEnhanced(clazz)){
                clazz = clazz.getSuperclass();
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Autowired.class)){
                        field.setAccessible(true);
                        try {
                            field.set(bean, context.getBean(field.getType()));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
