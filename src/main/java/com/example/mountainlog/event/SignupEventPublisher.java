package com.example.mountainlog.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.mountainlog.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SignupEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishSignupEvent(User user, String appUrl) {
        SignupEvent signupEvent = new SignupEvent(this, user, appUrl);
        applicationEventPublisher.publishEvent(signupEvent);
    }
}
