package com.panhandleirrigation.pivot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.panhandleirrigation.pivot.entity.Customer;
import com.panhandleirrigation.pivot.service.CustomerService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class DefaultCustomerController implements CustomerController {

	@Autowired
	private CustomerService customerService;

	@Override
	public List<Customer> fetchCustomers() {
		log.info("List of customers was requested");
		return customerService.fetchCustomers();
	}

	@Override
	public Customer updateCustomer(Customer customer) {
		log.info("Atempting to update customer with key " + customer.getPublicKey() + " to "
				+ customer.getCustomerName());
		return customerService.updateCustomer(customer);
	}

}
