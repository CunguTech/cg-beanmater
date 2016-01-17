package me.cungu.beanmater.util;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtil {
	
	private static ObjectMapper objectMapper = null;
	
	public static String toJson(Object obj) {
		if (obj == null) {
			return null;
		}
		
		try {
			return getObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	public static String toPrettyJson(Object obj) {
		if (obj == null) {
			return null;
		}
		
		try {
			return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	public static <T> T parseJson(String jsonStr, Class<T> valueType) {
		if (StringUtils.isBlank(jsonStr)) {
			return null;
		}
		
		try {
			return getObjectMapper().readValue(jsonStr, valueType);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	public static <T> T parseJson(String jsonStr, TypeReference<T> typeReference) {
		if (StringUtils.isBlank(jsonStr)) {
			return null;
		}
		
		try {
			return getObjectMapper().readValue(jsonStr, typeReference);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	private static ObjectMapper getObjectMapper() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
			objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
			objectMapper.setTimeZone(TimeZone.getDefault());
//			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//			objectMapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
//			objectMapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
			objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
			objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		}
		
		return objectMapper;
	}
	
	public void setObjectMapper(ObjectMapper om) {
		objectMapper = om;
	}
}