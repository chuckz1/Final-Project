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
public class CustomerPivot implements Comparable<CustomerPivot> {
	Long customerFK;
	Long pivotFK;
	
	@NotNull
	@Length(max = 10)
	@Pattern(regexp = "[\\w\\s]*")
	String publicKey;
	
	@NotNull
	@Length(max = 10)
	@Pattern(regexp = "[\\w\\s]*")
	String customerKey;
	
	@NotNull
	@Length(max = 10)
	@Pattern(regexp = "[\\w\\s]*")
	String pivotKey;

	@JsonIgnore
	public Long getCustomerFK() {
		return customerFK;
	}

	@JsonIgnore
	public Long getPivotFK() {
		return pivotFK;
	}

	@Override
	public int compareTo(CustomerPivot that) {
		return Comparator.comparing(CustomerPivot::getPublicKey).compare(this, that);
	}
}
