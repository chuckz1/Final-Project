package com.panhandleirrigation.pivot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.panhandleirrigation.pivot.entity.CustomerPivot;
import com.panhandleirrigation.pivot.service.CustomerPivotService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class DefaultCustomerPivotController implements CustomerPivotController {

	@Autowired
	CustomerPivotService customerPivotService;
	
	@Override
	public List<CustomerPivot> fetchCustomerPivotsByKey(String customerKey) {
		log.info("A list of CustomerPivots was requested for customer key " + customerKey);
		return customerPivotService.fetchCustomerPivotsByCustomerKey(customerKey);
	}

	@Override
	public CustomerPivot createCustomerPivot(CustomerPivot customerPivot) {
		log.info("A new customerPivot was requested for customer key " + customerPivot.getCustomerKey());
		return customerPivotService.createCustomerPivot(customerPivot);
	}

	@Override
	public void deleteCustomerPivot(String customerPivotKey) {
		log.info("A delete was requested for customer pivot key " + customerPivotKey);
		customerPivotService.deleteCustomerPivot(customerPivotKey);
	}

}
