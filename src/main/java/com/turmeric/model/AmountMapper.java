package com.turmeric.model;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.turmeric.constants.CollectionConstants;

@Document(collection = CollectionConstants.ITEMS_PRICE)
public class AmountMapper {
	@Id
	@JsonIgnore
	private String id;
	private Map<String, Map<Integer, Integer>> values;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, Map<Integer, Integer>> getValues() {
		return values;
	}

	public void setValues(Map<String, Map<Integer, Integer>> values) {
		this.values = values;
	}

}
