package zzw.demo.myspring.entity;

import lombok.Data;
import zzw.demo.myspring.core.ioc.annotation.Autowired;
import zzw.demo.myspring.core.ioc.annotation.Component;

@Data
@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private User user;
    @Override
    public String toString() {
        return "UserServiceImpl{" +
                "user=" + (user != null) +
                '}';
    }
}
