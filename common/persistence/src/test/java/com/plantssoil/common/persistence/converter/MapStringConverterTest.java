package com.plantssoil.common.persistence.converter;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class MapStringConverterTest {
	public static void main(String[] args) {
		MapStringConverterTest test = new MapStringConverterTest();
		test.testConvertToDatabaseColumn();
	}

	@Test
	public void testConvertToDatabaseColumn() {
		assertEquals(dbData(), new MapStringConverter().convertToDatabaseColumn(attributeMap()));
	}

	private Map<String, String> attributeMap() {
		Map<String, String> attribute = new LinkedHashMap<>();
		attribute.put("key1", "v1");
		attribute.put("key2", "v2");
		attribute.put("key3", "v3");
		attribute.put("key4", "v4");
		return attribute;
	}

	private String dbData() {
		return "{\"key1\":\"v1\",\"key2\":\"v2\",\"key3\":\"v3\",\"key4\":\"v4\"}";
	}

	@Test
	public void testConvertToEntityAttribute() {
		assertEquals(attributeMap(), new MapStringConverter().convertToEntityAttribute(dbData()));
	}

}
