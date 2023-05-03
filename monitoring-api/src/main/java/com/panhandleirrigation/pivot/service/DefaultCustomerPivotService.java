package com.panhandleirrigation.pivot.service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.panhandleirrigation.pivot.dao.CustomerDao;
import com.panhandleirrigation.pivot.dao.CustomerPivotDao;
import com.panhandleirrigation.pivot.dao.PivotDao;
import com.panhandleirrigation.pivot.entity.CustomerPivot;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DefaultCustomerPivotService implements CustomerPivotService {

	@Autowired
	CustomerDao customerDao;

	@Autowired
	PivotDao pivotDao;

	@Autowired
	CustomerPivotDao customerPivotDao;

	@Override
	public List<CustomerPivot> fetchCustomerPivotsByCustomerKey(String customerKey) {
		Long customerPK = customerDao.convertKeyToPK(customerKey)
				.orElseThrow(() -> new NoSuchElementException("Customer with key = " + customerKey + " was not found"));

		List<CustomerPivot> list = customerPivotDao.CustomerPivotsByCustomerPK(customerPK);

		for (CustomerPivot customerPivot : list) {
			customerPivot.setCustomerKey(customerKey);
			customerPivot.setPivotKey(
					pivotDao.convertPKtoString(customerPivot.getPivotFK()).orElseThrow(() -> new RuntimeException(
							"Pivot with key " + customerPivot.getPivotFK() + " could not be found")));
		}

		Collections.sort(list);

		return list;
	}

	@Override
	public CustomerPivot createCustomerPivot(CustomerPivot customerPivot) {
		customerPivot.setCustomerFK(
				customerDao.convertKeyToPK(customerPivot.getCustomerKey()).orElseThrow(() -> new NoSuchElementException(
						"Customer with key = " + customerPivot.getCustomerKey() + " was not found")));

		customerPivot.setPivotFK(
				pivotDao.convertKeyToPK(customerPivot.getPivotKey()).orElseThrow(() -> new NoSuchElementException(
						"Pivot with key = " + customerPivot.getPivotKey() + " was not found")));

		CustomerPivot result = customerPivotDao.createCustomerPivot(customerPivot)
				.orElseThrow(() -> new RuntimeException(
						"CustomerPivot with customer key " + customerPivot.getCustomerKey() + " could not be created"));

		result.setCustomerKey(
				customerDao.convertPKtoString(result.getCustomerFK()).orElseThrow(() -> new RuntimeException(
						"CustomerPivot with customer key " + result.getCustomerKey() + " could not be created")));

		result.setPivotKey(pivotDao.convertPKtoString(result.getPivotFK()).orElseThrow(() -> new RuntimeException(
				"CustomerPivot with customer key " + result.getCustomerKey() + " could not be created")));

		return result;

	}

	@Override
	public void deleteCustomerPivot(String customerPivotKey) {
		int count = customerPivotDao.deleteCustomerPivot(customerPivotKey);

		if (count < 1) {
			throw new DataIntegrityViolationException(
					"CustomerPivot with key " + customerPivotKey + " could not be deleted");
		}
	}

}
