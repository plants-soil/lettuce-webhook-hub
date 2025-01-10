package com.plantssoil.common.persistence.converter;

import javax.persistence.AttributeConverter;

/**
 * JVM array & persistence String field converter
 * 
 * @author danialdy
 */
public class ArrayStringConverter implements AttributeConverter<String[], String> {
	private final static String COMMA_REPLACEMENT = "【COMMA】";

	@Override
	public String convertToDatabaseColumn(String[] attribute) {
		if (attribute == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder().append("[");
		for (int i = 0; i < attribute.length; i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(attribute[i].replaceAll(",", COMMA_REPLACEMENT));
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public String[] convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.strip().length() == 0) {
			return new String[0];
		}
		String[] items = dbData.substring(1, dbData.length() - 1).split(",");
		for (int i = 0; i < items.length; i++) {
			if (items[i].indexOf(COMMA_REPLACEMENT) >= 0) {
				items[i] = items[i].replaceAll(COMMA_REPLACEMENT, ",");
			}
		}
		return items;
	}

}
