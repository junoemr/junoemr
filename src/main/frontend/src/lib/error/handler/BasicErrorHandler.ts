import {ErrorHandler} from "./ErrorHandler";

export default class BasicErrorHandler implements ErrorHandler {

	/**
	 * Optional behaviour to call on the error object.
	 *
	 * Subclasses extending this object should provide an implementation to this method.
	 * The Default implementation is to do nothing.
	 *
	 * @param response error response.  If from juno api, will have response.data.error fields.
	 */
	protected serviceError(response: any) {};

	/**
	 * Pass through error handler.  The error is logged, and the service function is called.
	 * Finally, the error is re-thrown.
	 */
	public handleError(response: any): any
	{
		console.error(response);
		this.serviceError(response);

		if (response && response.data && response.data.error)
		{
			throw response.data.error;
		}
		else
		{
			throw response;
		}
	}
}