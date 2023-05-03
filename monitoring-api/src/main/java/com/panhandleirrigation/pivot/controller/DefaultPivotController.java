package com.panhandleirrigation.pivot.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.panhandleirrigation.pivot.entity.Pivot;
import com.panhandleirrigation.pivot.service.PivotService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class DefaultPivotController implements PivotController {

	@Autowired
	PivotService pivotService;

	@Override
	public List<Pivot> fetchPivots() {
		log.info("List of pivots was requested");
		return pivotService.fetchPivots();
	}

	@Override
	public Pivot updatePivot(Pivot pivot) {
		log.info("Pivot is being updated: " + pivot.getPublicKey());
		return pivotService.updatePivot(pivot);
	}

	@Override
	public Pivot createPivot(Pivot pivot) {
		log.info("A new pivot is being created");
		return pivotService.createPivot(pivot);
	}

	@Override
	public void deletePivot(String pivotKey) {
		log.info("Deleting a pivot");
		pivotService.deletePivot(pivotKey);
	}


}
