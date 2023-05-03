package com.panhandleirrigation.pivot.dao;


import com.panhandleirrigation.pivot.entity.TableName;
import com.panhandleirrigation.pivot.entity.ValidKeyGenerated;

public interface HelperDao {

	ValidKeyGenerated verifyKey(String key, TableName table, String column);

}
