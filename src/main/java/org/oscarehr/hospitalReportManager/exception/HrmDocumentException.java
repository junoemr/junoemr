package org.oscarehr.hospitalReportManager.exception;

public class HrmDocumentException extends HrmException
{

	public HrmDocumentException(String message)
	{
		super(message);
	}

	public HrmErrorResponse toResponse()
	{
		return HrmErrorResponse.DOCUMENT_EXCEPTION;
	}
}
