package com.panhandleirrigation.pivot.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.panhandleirrigation.pivot.dao.CustomerDao;
import com.panhandleirrigation.pivot.entity.Customer;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DefaultCustomerService implements CustomerService {

	@Autowired
	private CustomerDao customerDao;

	@Transactional(readOnly = true)
	@Override
	public List<Customer> fetchCustomers() {
		log.info("Service Layer: customer list was requested");
		List<Customer> customers = customerDao.fetchCustomers();

		Collections.sort(customers);

		return customers;
	}

	@Transactional
	@Override
	public Customer updateCustomer(Customer customer) {
		log.info("Service Layer: Customer with key " + customer.getPublicKey() + " is being updated to "
				+ customer.getCustomerName());

		// save back the customer
		Customer result = customerDao.updateCustomer(customer).orElseThrow(() -> new RuntimeException(
				"Customer with key = " + customer.getPublicKey() + " could not be updated"));

		// return saved result
		return result;
	}

}
