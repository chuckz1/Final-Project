package com.panhandleirrigation.pivot.dao;

import java.util.List;
import java.util.Optional;

import com.panhandleirrigation.pivot.entity.CustomerPivot;

public interface CustomerPivotDao {

	List<CustomerPivot> CustomerPivotsByCustomerPK(Long customerPK);

	Optional<CustomerPivot> createCustomerPivot(CustomerPivot customerPivot);

	Optional<CustomerPivot> getCustomerPivotByPK(String customerPivotPK);

	int deleteCustomerPivot(String customerPivotKey);

}
