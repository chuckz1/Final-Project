package com.panhandleirrigation.pivot.service;

public interface HelperService {
	
	final int keyGenerateCountLimit = 10;

	String generateKey(String table, String column);

}
