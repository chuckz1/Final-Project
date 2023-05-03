package com.panhandleirrigation.pivot.dao;

import java.util.List;
import java.util.Optional;

import com.panhandleirrigation.pivot.entity.Customer;

public interface CustomerDao {

	List<Customer> fetchCustomers();

	Optional<Customer> getCustomerFromName(String targetName);

	Optional<Customer> updateCustomer(Customer target);

	Optional<Customer> getCustomerFromKey(String targetKey);

	Optional<Long> convertKeyToPK(String publicKey);
	
	Optional<String> convertPKtoString(Long customerPK);

}
