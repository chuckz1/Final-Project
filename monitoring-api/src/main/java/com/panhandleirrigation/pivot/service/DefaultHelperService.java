package com.panhandleirrigation.pivot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.panhandleirrigation.pivot.dao.HelperDao;
import com.panhandleirrigation.pivot.entity.TableName;
import com.panhandleirrigation.pivot.entity.ValidKeyGenerated;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DefaultHelperService implements HelperService {

	@Autowired
	HelperDao helperDao;

	@Override
	public String generateKey(String table, String column) {
		log.info("Service layer: new key requested for table: {}, and column: {}", table, column);
		
		//generate and check the key is unique
		String out = "";
		ValidKeyGenerated checkResult = ValidKeyGenerated.Invalid;
		for (int safetyCounter = 0; safetyCounter < keyGenerateCountLimit; safetyCounter++) {
			out = getAlphaNumericString(10);
			
			checkResult = helperDao.verifyKey(out, TableName.valueOf(table), column);
			if (checkResult == ValidKeyGenerated.Valid) {
				break;
			}else {
				log.info("Found invalid key: " + out);
			}
		}

		if (checkResult != ValidKeyGenerated.Valid) {
			throw new RuntimeException("Could not generate a key: " + checkResult.toString());
		}

		return out;
	}

	// creates the random keys
	String getAlphaNumericString(int n) {

		// choose a Character random from this String
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";

		// create StringBuffer size of AlphaNumericString
		StringBuilder sb = new StringBuilder(n);

		for (int i = 0; i < n; i++) {

			// generate a random number between
			// 0 to AlphaNumericString variable length
			int index = (int) (AlphaNumericString.length() * Math.random());

			// add Character one by one in end of sb
			sb.append(AlphaNumericString.charAt(index));
		}

		return sb.toString();
	}

}
