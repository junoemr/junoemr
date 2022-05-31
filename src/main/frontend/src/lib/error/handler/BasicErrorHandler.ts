import {ErrorHandler} from "./ErrorHandler";
import {LogLevel} from "./LogLevel";

export default class BasicErrorHandler implements ErrorHandler
{
	private readonly _rethrow: boolean;
	private readonly _logLevel: LogLevel;

	constructor(rethrow: boolean = false, logLevel: LogLevel = LogLevel.ERROR)
	{
		this._rethrow = rethrow;
		this._logLevel = logLevel;
	}

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
		this.logError(response);
		this.serviceError(response);

		if(this.rethrow)
		{
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

	protected logError(message: any)
	{
		switch (this.logLevel)
		{
			case LogLevel.CRITICAL: console.error(message); break;
			case LogLevel.ERROR: console.error(message); break;
			case LogLevel.WARN: console.warn(message); break;
			case LogLevel.INFO: console.info(message); break;
			case LogLevel.DEBUG: console.debug(message); break;
			case LogLevel.NONE: break;
		}
	}

	get rethrow(): boolean
	{
		return this._rethrow;
	}

	get logLevel(): LogLevel
	{
		return this._logLevel;
	}
}