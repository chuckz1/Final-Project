package com.panhandleirrigation.pivot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.panhandleirrigation.pivot.service.HelperService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class DefaultHelperController implements HelperController {
	
	@Autowired
	HelperService helperService;
	
	@Override
	public String generateKey(String table, String column) {
		log.info("New key was requested for table {}, and column {}", table, column);
		return helperService.generateKey(table, column);
	}

}
