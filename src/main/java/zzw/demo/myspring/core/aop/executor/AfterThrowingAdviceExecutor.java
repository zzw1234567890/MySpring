package zzw.demo.myspring.core.aop.executor;

import net.sf.cglib.proxy.MethodProxy;
import zzw.demo.myspring.core.aop.annotation.AfterThrowing;
import zzw.demo.myspring.core.aop.annotation.Bound;
import zzw.demo.myspring.core.ioc.annotation.Component;
import zzw.demo.myspring.core.ioc.annotation.Scope;
import zzw.demo.myspring.core.ioc.constant.ScopeConstant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
@Scope(ScopeConstant.PROTOTYPE)
@Bound(AfterThrowing.class)
public class AfterThrowingAdviceExecutor extends AdviceExecutor {
    @Override
    public void invoke() throws InvocationTargetException, IllegalAccessException {
        adviceMethod.invoke(adviceObj);
    }

    @Override
    Object execute(Object o, Method method, Object[] args, MethodProxy proxy, ExecutorChain chain) throws Throwable {
        Object object = null;
        try {
            object = chain.executeNext(o, method, args, proxy);
        }catch (Exception e){
            invoke();
        }
        return object;
    }

    @Override
    int getOrder() {
        return 4;
    }
}
