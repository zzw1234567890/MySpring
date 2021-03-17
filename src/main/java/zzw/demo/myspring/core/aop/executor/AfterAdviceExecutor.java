package zzw.demo.myspring.core.aop.executor;

import zzw.demo.myspring.core.aop.annotation.After;
import zzw.demo.myspring.core.aop.annotation.Bound;
import zzw.demo.myspring.core.ioc.annotation.Component;
import zzw.demo.myspring.core.ioc.annotation.Scope;
import zzw.demo.myspring.core.ioc.constant.ScopeConstant;

import java.lang.reflect.InvocationTargetException;

@Component
@Scope(ScopeConstant.PROTOTYPE)
@Bound(After.class)
public class AfterAdviceExecutor extends AdviceExecutor {
    @Override
    public void invoke() throws InvocationTargetException, IllegalAccessException {
        adviceMethod.invoke(adviceObj);
    }

    @Override
    int getOrder() {
        return 2;
    }
}
