package com.plantssoil.common.persistence.converter;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ArrayStringConverterTest {
	@Test
	public void testConvertToDatabaseColumn() {
		String[] items = { "192.168.0.1", "192,168,0,2", "this is the test", "192.168.0.3" };
		ArrayStringConverter c = new ArrayStringConverter();
		String dbData = c.convertToDatabaseColumn(items);
		assertEquals("[192.168.0.1,192【COMMA】168【COMMA】0【COMMA】2,this is the test,192.168.0.3]", dbData);
	}

	@Test
	public void testConvertToEntityAttribute() {
		String dbData = "[192.168.0.1,192【COMMA】168【COMMA】0【COMMA】2,this is the test,192.168.0.3]";
		ArrayStringConverter c = new ArrayStringConverter();
		String[] items = c.convertToEntityAttribute(dbData);
		assertArrayEquals(new String[] { "192.168.0.1", "192,168,0,2", "this is the test", "192.168.0.3" }, items);
	}

}
