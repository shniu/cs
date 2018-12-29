package com.digcredit.core.fsm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;

/**
 * Fsm configuration
 * Created by Administrator on 2018/12/28 0028.
 */
@Configuration
@EnableStateMachine
public class FsmConfig extends EnumStateMachineConfigurerAdapter<OrderStates, OrderEvents> {

}
