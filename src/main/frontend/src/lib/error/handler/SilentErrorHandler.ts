import {ErrorHandler} from "./ErrorHandler";

/**
 * simple error handler that prints error to console
 */
export default class SilentErrorHandler implements ErrorHandler
{
	public handleError(response: any): any
	{
		console.error(response);
		if(response && response.data && response.data.error)
		{
			throw response.data.error;
		}
		throw response;
	}
}