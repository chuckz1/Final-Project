package com.panhandleirrigation.pivot.entity;

import java.util.Comparator;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer implements Comparable<Customer> {
	private Long customerPK;
	
	@NotNull
	@Length(max = 10)
	@Pattern(regexp = "[\\w\\s]*")
	private String publicKey;
	
	@NotNull
	@Length(max = 30)
	@Pattern(regexp = "[\\w\\s]*")
	private String customerName;
	
	@JsonIgnore
	public Long getCustomerPK(){
		return customerPK;
	}

	@Override
	public int compareTo(Customer that) {
		return Comparator.comparing(Customer::getCustomerName).compare(this, that);
	}
}
