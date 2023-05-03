package com.panhandleirrigation.pivot.service;

import java.util.List;

import com.panhandleirrigation.pivot.entity.CustomerPivot;

public interface CustomerPivotService {

	List<CustomerPivot> fetchCustomerPivotsByCustomerKey(String customerKey);

	CustomerPivot createCustomerPivot(CustomerPivot customerPivot);

	void deleteCustomerPivot(String customerPivotKey);

}
