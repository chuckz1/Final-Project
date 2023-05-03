package com.panhandleirrigation.pivot.service;

import java.util.List;

import com.panhandleirrigation.pivot.entity.Customer;

public interface CustomerService {

	List<Customer> fetchCustomers();

	Customer updateCustomer(Customer customer);

}
