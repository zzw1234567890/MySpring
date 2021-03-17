package zzw.demo.myspring.core;

import lombok.Data;
import zzw.demo.myspring.core.ioc.base.BeanConfiguration;
import zzw.demo.myspring.core.ioc.base.BeanDefinition;
import zzw.demo.myspring.core.ioc.annotation.*;
import zzw.demo.myspring.core.ioc.bean.BeanNameAware;
import zzw.demo.myspring.core.ioc.bean.BeanPostProcessor;
import zzw.demo.myspring.core.ioc.bean.ContextPostProcessor;
import zzw.demo.myspring.core.ioc.bean.InitializingBean;
import zzw.demo.myspring.core.ioc.constant.ScopeConstant;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

@Data
@Component
public class AnnotationApplicationContext implements ApplicationContext{

    private Class<?> configClass;
    // bean的名称和定义信息映射表
    private Map<String, BeanDefinition> beanDefinitions = new HashMap<>();
    // 单例bean的名称的实例映射表
    private Map<String, Object> singletonObjects = new HashMap<>();
    // 创建bean时的二级缓存
    private Map<String, Object> earlySingletonObjects = new HashMap<>();
    // 配置bean的定义信息集合
    private Set<BeanDefinition> configBeans = new HashSet<>();

    public AnnotationApplicationContext(Class<?>... clazz) {
        init();
        for (Class<?> aClass : clazz) {
            if (aClass.isAnnotationPresent(Configuration.class)) {
                configClass = aClass;
                scanBean();
            }
        }
        explainConfiguration();
        instanceBefore();
        instanceSingletonBean();
        instanceAfter();
    }

    /**
     *  创建单例bean开始之前，调用ContextPostProcessor.afterContextInitialization
     */

    private void instanceBefore() {
        for (BeanDefinition beanDefinition : configBeans) {
            if (ContextPostProcessor.class.isAssignableFrom(beanDefinition.getBeanClass())){
                Object o = singletonObjects.get(beanDefinition.getBeanName());
                ((ContextPostProcessor) o).beforeContextInitialization(this);
            }
        }
    }

    /**
     * 单例bean创建完之后，调用ContextPostProcessor.beforeContextInitialization
     */
    private void instanceAfter() {
        for (BeanDefinition beanDefinition : configBeans) {
            if (ContextPostProcessor.class.isAssignableFrom(beanDefinition.getBeanClass())){
                Object o = singletonObjects.get(beanDefinition.getBeanName());
                ((ContextPostProcessor) o).afterContextInitialization(this);
            }
        }
    }

    /**
     * 初始化Application
     * 扫描核心库中的bean
     * 把当前对象加入到容器中
     */
    private void init() {
        doScan(this.getClass().getPackage().getName());
        String beanName = AnnotationApplicationContext.class.getName();
        singletonObjects.put(beanName, this);
    }

    /**
     * 解析并创建配置类，调用配置的config方法
     */
    private void explainConfiguration() {
        // 创建配置类的实例，并调用config方法
        for (BeanDefinition beanDefinition : configBeans) {
            Object bean = createBean(beanDefinition);
            singletonObjects.put(beanDefinition.getBeanName(), bean);
            if (BeanConfiguration.class.isAssignableFrom(beanDefinition.getBeanClass())){
                ((BeanConfiguration) bean).config();
            }
        }
    }

    /**
     * 实例化所有单例bean
     */
    private void instanceSingletonBean() {
        for (String key : beanDefinitions.keySet()) {
            BeanDefinition beanDefinition = beanDefinitions.get(key);
            if (singletonObjects.get(key) == null && !beanDefinition.isLazy()
                    && beanDefinition.getScope() == ScopeConstant.SINGLETON) {
                singletonObjects.put(key, createBean(beanDefinition));
            }
        }
    }

    /**
     * 根据beanDefinition创建对应的bean
     *
     * @param beanDefinition
     * @return
     */
    private Object createBean(BeanDefinition beanDefinition) {
        Object bean = null;
        String beanName = beanDefinition.getBeanName();
        try {
            bean = earlySingletonObjects.get(beanName);
            if (bean != null) {
                return bean;
            }
            bean = beanDefinition.getBeanClass().getDeclaredConstructor().newInstance();
            // 把实例化后的bean放入二级缓存
            earlySingletonObjects.put(beanName, bean);
            bean = doBeanPostProcessorBefore(bean, beanName);
            initBean(bean, beanDefinition);
            bean = doBeanPostProcessorAfter(bean, beanName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        earlySingletonObjects.put(beanName, null);
        return bean;
    }

    /**
     * 调用后置处理器初始化前方法
     * @param bean
     * @param beanName
     * @return
     */
    private Object doBeanPostProcessorBefore(Object bean, String beanName) {
        if (configBeans.contains(beanDefinitions.get(beanName))){
            return bean;
        }
        for (BeanDefinition beanDefinition : configBeans) {
            if (BeanPostProcessor.class.isAssignableFrom(beanDefinition.getBeanClass())) {
                Object o = singletonObjects.get(beanDefinition.getBeanName());
                bean = ((BeanPostProcessor) o).postProcessBeforeInitialization(bean, beanName);
            }
        }
        return bean;
    }

    /**
     * 调用后置处理器初始化后方法
     * @param bean
     * @param beanName
     * @return
     */
    private Object doBeanPostProcessorAfter(Object bean, String beanName) {
        if (configBeans.contains(beanDefinitions.get(beanName))){
            return bean;
        }
        for (BeanDefinition beanDefinition : configBeans) {
            if (BeanPostProcessor.class.isAssignableFrom(beanDefinition.getBeanClass())) {
                Object o = singletonObjects.get(beanDefinition.getBeanName());
                bean = ((BeanPostProcessor) o).postProcessAfterInitialization(bean, beanName);
            }
        }
        return bean;
    }

    /**
     * bean初始化
     *
     * @param bean
     * @param beanDefinition
     * @throws IllegalAccessException
     */
    private void initBean(Object bean, BeanDefinition beanDefinition) throws IllegalAccessException {
        String beanName = beanDefinition.getBeanName();
        // 属性注入
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                field.setAccessible(true);
                field.set(bean, getBean(field.getType()));
            }
        }
        // 调用 BeanNameAware 设置 BeanName
        if (bean instanceof BeanNameAware) {
            ((BeanNameAware) bean).setBeanName(beanName);
        }
        // 调用 InitializingBean 的 afterPropertiesSet 方法执行自定义的初始化逻辑
        if (bean instanceof InitializingBean) {
            ((InitializingBean) bean).afterPropertiesSet();
        }
    }

    /**
     * 保证ioc在容器创建完之后多线程环境下bean创建的安全性
     * @param beanDefinition
     * @return
     */
    private synchronized Object createSingletonBeanSafe(BeanDefinition beanDefinition) {
        Object bean = singletonObjects.get(beanDefinition.getBeanName());
        if (bean == null) {
            bean = createBean(beanDefinition);
            singletonObjects.put(beanDefinition.getBeanName(), bean);
        }
        return bean;
    }

    /**
     * 根据beanName获取bean
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitions.get(beanName);
        switch (beanDefinition.getScope()) {
            case SINGLETON:
            case REQUEST:
            case SESSION:
                Object bean = singletonObjects.get(beanName);
                return bean != null ? bean : createSingletonBeanSafe(beanDefinition);
            case PROTOTYPE:
                return createBean(beanDefinition);
            default:
                return null;
        }
    }

    /**
     * 根据class获取bean
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getBean(Class<T> clazz) {
        for (String key : beanDefinitions.keySet()) {
            Class<?> beanClass = beanDefinitions.get(key).getBeanClass();
            if (beanClass == clazz || (clazz.isInterface() && clazz.isAssignableFrom(beanClass))) {
                return (T) getBean(key);
            }
        }
        return null;
    }

    /**
     * 根据配置类的ComponentScan注解加载bean，并放入Set<BeanDefinition>中
     */
    private void scanBean() {
        // 获取ComponentScan注解扫描包的路径
        ComponentScan configClassAnnotation = configClass.getAnnotation(ComponentScan.class);
        String[] packageNames = configClassAnnotation.value();
        for (String packageName : packageNames) {
            doScan(packageName);
        }
    }

    /**
     * 扫描指定的包名
     *
     * @param packageName
     */
    private void doScan(String packageName) {
        // 通过类加载器获取class文件路径
        ClassLoader classLoader = AnnotationApplicationContext.class.getClassLoader();
        URL url = classLoader.getResource(packageName.replace(".", "/"));
        if (url == null) {
            return;
        }
        // 解决单元测试时扫描包的路径问题
        String path = url.getPath().replaceFirst("out/test", "out/production");
        File dir = new File(path);
        // File dir = new File(url.getFile());
        // 加载要扫描包下的类
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()){
                doScan(packageName + "." + file.getName());
            }else {
                try {
                    Class<?> aClass = classLoader.loadClass(packageName + "." + file.getName().substring(0, file.getName().indexOf(".")));
                    if (isBean(aClass)) {
                        registerBeanDefinition(aClass);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 判断传入class是否为bean
     * @param aClass
     * @return
     */
    private boolean isBean(Class<?> aClass){
        return !aClass.isAnnotation() && !aClass.isInterface() && isExtendsComponent(aClass);
    }

    /**
     * 递归判断传入class是否为bean
     * 使用了@Component注解以及@Component的子注解
     * @param aClass
     * @return
     */
    private boolean isExtendsComponent(Class<?> aClass) {
        if (aClass.isAnnotationPresent(Component.class)){
            return true;
        }
        Annotation[] annotations = aClass.getAnnotations();
        if (annotations.length == 0){
            return false;
        }
        boolean res = false;
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType == Retention.class || annotationType == Target.class || annotationType == Documented.class){
                continue;
            }
            res = isExtendsComponent(annotationType);
            if (res){
                return true;
            }
        }
        return res;
    }

    /**
     * 注册bean的定义类
     *
     * @param clazz
     * @return
     */
    private void registerBeanDefinition(Class<?> clazz) {
        String beanName = "";
        if (clazz.isAnnotationPresent(Component.class)){
            beanName = clazz.getAnnotation(Component.class).value();
        }
        ScopeConstant scope = ScopeConstant.SINGLETON;
        if (clazz.isAnnotationPresent(Scope.class)) {
            scope = clazz.getAnnotation(Scope.class).value();
        }
        if (clazz.isAnnotationPresent(Configuration.class)){
            scope = ScopeConstant.SINGLETON;
        }
        beanName = beanName.equals("") ? clazz.getName() : beanName;
        BeanDefinition beanDefinition = new BeanDefinition(clazz, beanName, scope, clazz.isAnnotationPresent(Lazy.class));
        if (BeanConfiguration.class.isAssignableFrom(clazz) || clazz.isAnnotationPresent(Configuration.class)){
            beanDefinition.setScope(ScopeConstant.SINGLETON);
            configBeans.add(beanDefinition);
        }
        beanDefinitions.put(beanName, beanDefinition);
    }

    @Override
    public String toString() {
        return "ApplicationContext{" +
                "configClass=" + configClass +
                ", beanDefinitions=" + beanDefinitions +
                ", configBeans=" + configBeans +
                '}';
    }
}
