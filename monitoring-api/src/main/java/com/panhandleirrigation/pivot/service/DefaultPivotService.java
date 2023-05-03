package com.panhandleirrigation.pivot.service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.panhandleirrigation.pivot.dao.PivotDao;
import com.panhandleirrigation.pivot.entity.Pivot;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DefaultPivotService implements PivotService {

	@Autowired
	PivotDao pivotDao;

	@Override
	public List<Pivot> fetchPivots() {
		log.info("Service Layer: List of pivots was requested");
		List<Pivot> list = pivotDao.fetchPivots();

		Collections.sort(list);

		return list;
	}

	@Override
	public Pivot updatePivot(Pivot pivot) {
		log.info("Service Layer: pivot is being updated: " + pivot.getPublicKey());
		
		pivot.setPivotPK(pivotDao.convertKeyToPK(pivot.getPublicKey()).orElseThrow(
				() -> new NoSuchElementException("Pivot with key = " + pivot.getPublicKey() + " could not be found")));
		
		return pivotDao.updatePivot(pivot).orElseThrow(
				() -> new NoSuchElementException("Pivot with key = " + pivot.getPublicKey() + " could not be updated"));
	}

	@Override
	public Pivot createPivot(Pivot pivot) {
		log.info("Service lauer: A new pivot is being created");
		return pivotDao.createPivot(pivot).orElseThrow(
				() -> new NoSuchElementException("Pivot with key = " + pivot.getPublicKey() + " could not be created"));
	}

	@Override
	public void deletePivot(String pivotKey) {
		log.info("Service layer: deleting pivot with key " + pivotKey);
		Long pivotPK = pivotDao.convertKeyToPK(pivotKey)
				.orElseThrow(() -> new NoSuchElementException("Pivot with key = " + pivotKey + " could not be found"));
		int count = pivotDao.deletePivot(pivotPK);

		if (count < 1) {
			throw new DataIntegrityViolationException("Pivot with key " + pivotKey + " could not be deleted");
		}
	}

}
