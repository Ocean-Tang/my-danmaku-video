package com.study.mydanmakuvideo.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

    public static final String DANAMKU_EXCHANGE = "danmaku-exchange";
    public static final String DANAMKU_QUEUE = "danmaku-queue";

    @Bean
    public Queue danmakuQueue() {
        return new Queue(DANAMKU_QUEUE);
    }

    @Bean
    public FanoutExchange danmakuExchange() {
        return new FanoutExchange(DANAMKU_EXCHANGE);
    }

    @Bean
    public Binding danmakuBinding() {
        return BindingBuilder.bind(danmakuQueue()).to(danmakuExchange());
    }

}