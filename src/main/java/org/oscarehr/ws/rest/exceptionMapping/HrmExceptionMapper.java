package org.oscarehr.ws.rest.exceptionMapping;

import org.apache.log4j.Logger;
import org.oscarehr.hospitalReportManager.exception.HrmException;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.response.RestResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class HrmExceptionMapper implements ExceptionMapper<HrmException>
{
	private static final Logger logger = MiscUtils.getLogger();

	@Override
	public Response toResponse(HrmException exception)
	{
		String message = exception.toResponse().name();
		logger.error(message, exception);

		RestResponse<String> response = RestResponse.errorResponse(message);

		return Response.status(Response.Status.BAD_REQUEST).entity(response)
			.type(MediaType.APPLICATION_JSON).build();
	}
}
