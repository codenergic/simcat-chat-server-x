package org.codenergic.simcat.core.web;

import javax.ws.rs.BadRequestException;

import org.apache.commons.lang3.StringUtils;

public interface Validator {
	static void isNotBlank(String s, String message) {
		if (StringUtils.isBlank(s))
			throw new BadRequestException(message);
	}

	static void isNotNull(Object o, String message) {
		if (o == null)
			throw new BadRequestException(message);
	}
}
