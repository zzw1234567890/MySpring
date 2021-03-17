package zzw.demo.myspring.core.aop.executor;

import lombok.Data;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 通知执行链，所有的通知在此处进行调用
 * 使用责任链设计模式
 */
@Data
public class ExecutorChain {
    // 当前执行链正在执行的顺序
    private int currIndex = -1;
    // 通知处理器列表
    private List<AdviceExecutor> adviceList = new ArrayList<>();
    // 允许通知的方法集合
    private Set<Method> allowMethodSet = new HashSet<>();

    public void sortAdvice(){
        adviceList.sort(Comparator.comparingInt(AdviceExecutor::getOrder));
    }

    public Object executeNext(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (++currIndex < adviceList.size()) {
            return adviceList.get(currIndex).execute(obj, method, args, proxy, this);
        }
        // 执行原始方法
        return proxy.invokeSuper(obj, args);
    }
}
