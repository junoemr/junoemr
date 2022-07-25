import {ErrorHandler} from "./ErrorHandler";
import {LogLevel} from "./LogLevel";
import GenericError from "../GenericError";
import BaseError from "../BaseError";

export default class BasicErrorHandler implements ErrorHandler
{
	private readonly _rethrow: boolean;
	private readonly _logLevel: LogLevel;

	/**
	 * constructor with global options initialization
	 * @param rethrow - rethrow errors after handing them. for use where rejecting a promise is
	 * still required (ie service layer). default false.
	 * @param logLevel - default log level for console logging. default LogLevel.ERROR
	 */
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
	 * Finally, the error is re-thrown (if the rethrow option is true).
	 * @param response - the error object
	 * @param logLevel - log level for console logging. defaults to the global log level
	 */
	public handleError(response: any, logLevel: LogLevel = this._logLevel): any
	{
		this.logError(response, logLevel);
		this.serviceError(response);

		if(this.rethrow)
		{
			if (response instanceof BaseError)
			{
				throw response;
			}
			if (response && response.data && response.data.error)
			{
				throw new GenericError(response.data.error);
			}
			else
			{
				throw new GenericError(response);
			}
		}
	}

	protected logError(message: any, logLevel: LogLevel)
	{
		switch (logLevel)
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