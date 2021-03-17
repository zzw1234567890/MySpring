package zzw.demo.myspring.core.aop.executor;

import lombok.Data;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 通知执行器抽象类
 * 使用模板方法设计模式
 */
@Data
public abstract class AdviceExecutor {
    // 该通知调用的方法
    protected Method adviceMethod;
    // 该通知调用的对象
    protected Object adviceObj;

    abstract void invoke() throws InvocationTargetException, IllegalAccessException;

    // 返回通知的执行优先级
    abstract int getOrder();

    Object execute(Object o, Method method, Object[] args, MethodProxy proxy, ExecutorChain chain) throws Throwable {
        invoke();
        return chain.executeNext(o, method, args, proxy);
    }
}
