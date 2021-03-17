package zzw.demo.myspring.core.aop.base;

import net.sf.cglib.proxy.Enhancer;
import zzw.demo.myspring.core.AnnotationApplicationContext;
import zzw.demo.myspring.core.aop.annotation.*;
import zzw.demo.myspring.core.aop.executor.*;
import zzw.demo.myspring.core.ioc.annotation.Autowired;
import zzw.demo.myspring.core.ioc.annotation.Configuration;
import zzw.demo.myspring.core.ioc.base.BeanConfiguration;
import zzw.demo.myspring.core.ioc.base.BeanDefinition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Configuration
public class AspectConfig implements BeanConfiguration {
    @Autowired
    private AnnotationApplicationContext context;
    // 注解和通知执行器的映射表
    private final Map<String, Class<? extends AdviceExecutor>> executorMap = new HashMap<>();

    /**
     * Aspect配置，由IOC容器初始化时自动调用
     */
    @Override
    public void config() {
        init();
        Map<String, BeanDefinition> beanDefinitions = context.getBeanDefinitions();
        for (String key : beanDefinitions.keySet()) {
            Class<?> beanClass = beanDefinitions.get(key).getBeanClass();
            if (beanClass.isAnnotationPresent(Aspect.class)){
                try {
                    executeAspect(beanClass);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 初始化，加入默认定义的通知
     */
    private void init() {
        Map<String, BeanDefinition> beanDefinitions = context.getBeanDefinitions();
        for (String beanName : beanDefinitions.keySet()) {
            Class<?> beanClass = beanDefinitions.get(beanName).getBeanClass();
            if (AdviceExecutor.class.isAssignableFrom(beanClass) && beanClass.isAnnotationPresent(Bound.class)){
                executorMap.put(beanClass.getAnnotation(Bound.class).value().getName(),
                        (Class<? extends AdviceExecutor>) beanClass);
            }
        }
    }

    /**
     * 处理Aspect的逻辑
     * @param beanClass
     */
    private void executeAspect(Class<?> beanClass) throws Exception{
        ExecutorChain chain = new ExecutorChain();
        for (Method method : beanClass.getDeclaredMethods()) {
            if (method.getAnnotations().length == 0){
                continue;
            }
            Class<? extends Annotation> annotationType = method.getAnnotations()[0].annotationType();
            // 加入通知
            if (annotationType.isAnnotationPresent(Advice.class)){
                AdviceExecutor adviceExecutor = executorMap.get(annotationType.getName()).newInstance();
                method.setAccessible(true);
                adviceExecutor.setAdviceMethod(method);
                adviceExecutor.setAdviceObj(beanClass.newInstance());
                chain.getAdviceList().add(adviceExecutor);
            }
            // 解析切入点
            if (method.isAnnotationPresent(PointCut.class)){
                explainPointCut(chain, method.getAnnotation(PointCut.class));
            }
        }
        // 对通知的执行顺序进行排序
        chain.sortAdvice();
    }

    /**
     * 解析切入点，创建代理对象，添加允许通知的方法
     * @param chain
     * @param pointCut
     */
    private void explainPointCut(ExecutorChain chain, PointCut pointCut) throws NoSuchMethodException{
        BeanDefinition beanDefinition = context.getBeanDefinitions().get(pointCut.beanName());
        String methodName = pointCut.method();
        Class<?> beanClass = beanDefinition.getBeanClass();
        Set<Method> allowMethodSet = chain.getAllowMethodSet();
        if ("*".equals(methodName)){
            Collections.addAll(allowMethodSet, beanClass.getDeclaredMethods());
        }else{
            allowMethodSet.add(beanClass.getMethod(pointCut.method()));
        }
        createProxyBean(chain, pointCut.beanName());
    }

    /**
     * 创建代理对象，但未初始化，在ioc容器创建完毕之后对代理类统一初始化
     * 同时把ExecuteChain对象放入ExecutorProxy对象中
     * @param beanName
     */
    private void createProxyBean(ExecutorChain chain, String beanName) {
        Object bean = null;
        try {
            bean = context.getBeanDefinitions().get(beanName).getBeanClass().getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(bean.getClass());
        ExecutorProxy executorProxy = new ExecutorProxy();
        executorProxy.setChain(chain);
        enhancer.setCallback(executorProxy);
        context.getSingletonObjects().put(beanName, enhancer.create());
    }

    public AspectConfig registerAdviceExecutor(Class<? extends Annotation> clazz, Class<? extends AdviceExecutor> executor){
        this.executorMap.put(clazz.getName(), executor);
        return this;
    }
}
