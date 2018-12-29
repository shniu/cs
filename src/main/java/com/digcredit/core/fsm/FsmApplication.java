package com.digcredit.core.fsm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author shniu
 */
@SpringBootApplication
@Slf4j
public class FsmApplication {

	public static void main(String[] args) {
		SpringApplication.run(FsmApplication.class, args);
		log.info("FSM 应用启动成功!");
	}

}

