package com.panhandleirrigation.pivot.service;

import java.util.List;

import com.panhandleirrigation.pivot.entity.Pivot;

public interface PivotService {

	List<Pivot> fetchPivots();

	Pivot updatePivot(Pivot pivot);

	Pivot createPivot(Pivot pivot);

	void deletePivot(String pivotKey);

}
