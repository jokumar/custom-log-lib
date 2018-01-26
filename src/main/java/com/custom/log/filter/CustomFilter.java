package com.custom.log.filter;

import java.io.IOException;
import java.net.InetAddress;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.custom.log.constants.ApplicationConstants;


@Component
public class CustomFilter implements Filter {
	@Resource
	private Environment env;

	public void init(FilterConfig arg0) throws ServletException {
		// Empty init.
	}

	public void destroy() {
		// Empty destroy.
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		try {
			MDC.put(ApplicationConstants.HOST, InetAddress.getLocalHost().getHostName());
			MDC.put(ApplicationConstants.PORT, String.valueOf(request.getServerPort()));
			MDC.put(ApplicationConstants.TXN_IDENTIFIER, request.getHeader(ApplicationConstants.REQUEST_ID));
			MDC.put(ApplicationConstants.CHANNEL, request.getHeader(ApplicationConstants.CLIENT_ID));
			MDC.put(ApplicationConstants.COMPONENT_NAME, env.getProperty(ApplicationConstants.COMPONENT_NAME));
			MDC.put(ApplicationConstants.REMOTE_ADDR, request.getHeader(ApplicationConstants.REMOTE_ADDR));
			setProfileCategory();
			chain.doFilter(request, response);
		} finally {
			MDC.clear();
		}
	}

	private String setProfileCategory() {
		String[] profiles = env.getActiveProfiles();
		if (profiles.length == 0) {

			profiles = env.getDefaultProfiles();
		}
		MDC.put(ApplicationConstants.ENV, profiles[0]);
		return profiles[0];
	}
}
