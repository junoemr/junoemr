/**
 * common error handler interface
 */
export interface ErrorHandler
{
	handleError(error: any): any;
}