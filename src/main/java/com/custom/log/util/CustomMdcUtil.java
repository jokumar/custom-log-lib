package com.custom.log.util;

import java.util.Map;

import org.slf4j.MDC;

import com.custom.log.constants.ApplicationConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomMdcUtil {
	/**
	 * Setter to add KeyInfo properties.
	 * @param keyInfo
	 */
	public static void setKeyInfo(Map<String, String> keyInfo) {
		MDC.put(ApplicationConstants.KEY_INFO, keyInfo.toString());
	}
	
	/**
	 * Override toString Request Dto.
	 * @param request
	 */
	public static void setRequest(Object request) {
		if (null != request) {
			MDC.put(ApplicationConstants.REQUEST, request.toString());
		}
	}
	
	/**
	 * Override toString Response Dto.
	 * @param response
	 */
	public static void setResponse(Object response) {
		if (null != response) {
			MDC.put(ApplicationConstants.RESPONSE, response.toString());
		}
	}
	
	
	/**
	 * For Storing the request temporarily 
	 * * @param request
	 */
	public static void setTempRequest(Object request) {
		if (null != request) {
			MDC.put(ApplicationConstants.TEMP_REQUEST, prettyJsonPrint(request, request.getClass()));
		}
	}
	
	/**
	 * For getting the temp request.
	 * * @param request
	 */
	public static String getTempRequest() {
		return MDC.get(ApplicationConstants.TEMP_REQUEST);
	}
	
	/**
	 * Convert the Object to JSON.
	 * 
	 * @param object
	 * @param clazz
	 * @return
	 */
	public static String prettyJsonPrint(final Object object, final Class clazz) {
		final ObjectMapper objectMapper = new ObjectMapper();
		String jsonStr = "";
		try {
			final Object jsonObj = objectMapper.readValue(objectMapper.writeValueAsString(object), clazz);
			jsonStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObj);
		} catch (final Exception exception) {
		}
		return jsonStr;
	}
	
	/**
	 * ErrorCode and ErrorMsg setter.
	 * @param code
	 * @param message
	 */
	public static void setErrorParam(String code, String message) {
		MDC.put(ApplicationConstants.ERROR_CODE, code);
		MDC.put(ApplicationConstants.ERROR_MSG, message);
	} 
}
