package com.example.license;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class DateSerializer extends JsonSerializer<Date> {

	@Override
	public void serialize(Date value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {

		String formatValue = dateFormat(value);
		jgen.writeString(formatValue);
	}

	public static String dateFormat(Date value) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String formatValue = format.format(value);
		return formatValue;
	}
}
