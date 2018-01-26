package com.custom.log;

import javax.annotation.Resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.custom.log.constants.ApplicationConstants;

@Aspect
@Component
public class CustomLogAspect {
	private static final Logger LOG = LoggerFactory.getLogger(CustomLogAspect.class);

	@Resource
	Environment env;

	@Around("@annotation(CustomLog)")
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		CustomLog customLog = signature.getMethod().getAnnotation(CustomLog.class);

		StringBuilder service = new StringBuilder();
		service.append(joinPoint.getTarget().getClass().getName()).append(".").append(signature.getName());

		String serviceName = customLog.serviceName();
		if (StringUtils.isEmpty(serviceName)) {
			serviceName = service.toString();
		}

		MDC.put(ApplicationConstants.SERVICE_NAME, serviceName);

		long start = System.currentTimeMillis();
		Object proceed = joinPoint.proceed();
		long executionTime = System.currentTimeMillis() - start;

		final StringBuilder logMessage = new StringBuilder();
		logMessage.append(service).append(" timing:" + executionTime);

		MDC.put(ApplicationConstants.EXECUTION_TIME, String.valueOf(executionTime));

		LOG.info(logMessage.toString());

		MDC.remove(ApplicationConstants.SERVICE_NAME);
		MDC.remove(ApplicationConstants.EXECUTION_TIME);
		MDC.remove(ApplicationConstants.KEY_INFO);
		MDC.remove(ApplicationConstants.REQUEST);
		MDC.remove(ApplicationConstants.TEMP_REQUEST);
		MDC.remove(ApplicationConstants.RESPONSE);
		MDC.remove(ApplicationConstants.ERROR_CODE);
		MDC.remove(ApplicationConstants.ERROR_MSG);

		return proceed;
	}
}