package org.oscarehr.common.conversion;

import org.springframework.beans.BeanUtils;

/**
 * peforms a basic bean copy conversion from object F to object T
 * @param <F>
 * @param <T>
 */
public class GenericConverter <F, T> extends AbstractModelConverter<F, T>
{
	private Class<T> toClass = null;

	public GenericConverter(Class<T> toClass)
	{
		this.toClass = toClass;
	}

	@Override
	public T convert(F input)
	{
		try
		{
			T sessionInfo = toClass.newInstance();
			BeanUtils.copyProperties(input, sessionInfo);
			return sessionInfo;
		}
		catch (IllegalAccessException | InstantiationException e)
		{
			throw new RuntimeException("Failed to Convert list of entities with error: " + e.toString(), e);
		}
	}
}
