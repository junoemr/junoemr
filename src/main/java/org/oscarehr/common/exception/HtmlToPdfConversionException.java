package org.oscarehr.common.exception;

public class HtmlToPdfConversionException extends Exception
{
	public HtmlToPdfConversionException()
	{
		super();
	}

	public HtmlToPdfConversionException(String message)
	{
		super(message);
	}

	public HtmlToPdfConversionException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
