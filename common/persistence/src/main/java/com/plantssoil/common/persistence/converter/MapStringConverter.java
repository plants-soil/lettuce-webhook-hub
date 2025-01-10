package com.plantssoil.common.persistence.converter;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.AttributeConverter;

import com.plantssoil.common.io.ObjectJsonSerializer;

/**
 * JVM map & persistence String field converter
 * 
 * @author danialdy
 */
public class MapStringConverter implements AttributeConverter<Map<String, String>, String> {

	@Override
	public String convertToDatabaseColumn(Map<String, String> attribute) {
		if (attribute == null) {
			return null;
		}
		return ObjectJsonSerializer.getInstance().serialize(attribute);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.strip().length() == 0) {
			return new LinkedHashMap<>();
		}
		return ObjectJsonSerializer.getInstance().unserialize(dbData, LinkedHashMap.class);
	}

}
