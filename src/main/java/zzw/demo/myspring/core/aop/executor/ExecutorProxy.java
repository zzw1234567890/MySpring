package zzw.demo.myspring.core.aop.executor;

import lombok.Data;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * cglib动态代理的对象
 * 此处检测调用的方法是否需要调用通知（充当方法过滤器）
 */
@Data
public class ExecutorProxy implements MethodInterceptor {
    private ExecutorChain chain;

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (chain.getAllowMethodSet().contains(method)) {
            chain.setCurrIndex(-1);
            return chain.executeNext(obj, method, args, proxy);
        }
        return proxy.invokeSuper(obj, args);
    }
}
