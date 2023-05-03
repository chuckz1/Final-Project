package com.panhandleirrigation.pivot.dao;

import java.util.List;
import java.util.Optional;

import com.panhandleirrigation.pivot.entity.Pivot;

public interface PivotDao {

	List<Pivot> fetchPivots();
	
	Optional<Pivot> getPivotByPK(Long contactID);

	Optional<Pivot> updatePivot(Pivot pivot);

	Optional<Long> convertKeyToPK(String publicKey);

	Optional<String> convertPKtoString(Long customerPK);

	Optional<Pivot> createPivot(Pivot pivot);

	int deletePivot(Long pivotPK);
}
