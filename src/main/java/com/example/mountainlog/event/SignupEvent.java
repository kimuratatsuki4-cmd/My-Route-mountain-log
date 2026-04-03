package com.example.mountainlog.event;

import org.springframework.context.ApplicationEvent;
import com.example.mountainlog.entity.User;
import lombok.Getter;

@Getter
public class SignupEvent extends ApplicationEvent {
    private final User user;
    private final String appUrl;

    public SignupEvent(Object source, User user, String appUrl) {
        super(source);
        this.user = user;
        this.appUrl = appUrl;
    }
}
