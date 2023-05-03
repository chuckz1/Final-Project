package com.panhandleirrigation.pivot.entity;

import java.util.Comparator;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

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
public class Contact implements Comparable<Contact> {
	private Long contactPK;
	private Long customerFK;
	
	@NotNull
	@Positive
	private int contactIndex;

	@NotNull
	@Length(max = 30)
	@Pattern(regexp = "[\\w\\s]*")
	private String description;

	@NotNull
	@Length(max = 30)
	@Pattern(regexp = "[\\w]+@[\\w]+\\.[\\w]{2,6}")
	private String email;
	
	@JsonIgnore
	public Long getContactPK() {
		return contactPK;
	}

	@Override
	public int compareTo(Contact that) {
		return Comparator.comparing(Contact::getDescription).thenComparing(Contact::getDescription).compare(this, that);
	}
}
