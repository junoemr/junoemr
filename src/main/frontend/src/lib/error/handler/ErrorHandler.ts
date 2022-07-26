import {LogLevel} from "./LogLevel";

/**
 * common error handler interface
 */
export interface ErrorHandler
{
	handleError(error: any, logLevel?: LogLevel): any;
}