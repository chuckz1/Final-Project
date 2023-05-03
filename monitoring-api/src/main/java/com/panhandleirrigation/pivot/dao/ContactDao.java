package com.panhandleirrigation.pivot.dao;

import java.util.List;
import java.util.Optional;

import com.panhandleirrigation.pivot.entity.Contact;

public interface ContactDao {

	List<Contact> getContactsByCustomerFK(Long customerPK);
	
	Optional<Contact> getContactByCustomerFKandPK(Long customerPK, Long contactPK);

	Optional<Contact> updateContact(Contact target);
	
	Optional<Contact> getContactByPK(Long contactPK);

	Optional<Contact> createContact(Contact contact);

	int deleteContact(Long customerPK, Long contactPK);
	
	Optional<Long> convertKeyToPK(String publicKey);
	
	Optional<String> convertPKtoString(Long contactPK);

	
}
