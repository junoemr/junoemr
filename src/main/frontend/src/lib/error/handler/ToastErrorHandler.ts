import ToastService from "../../alerts/service/ToastService";
import BasicErrorHandler from "./BasicErrorHandler";
import BaseError from "../BaseError";
import {LogLevel} from "./LogLevel";

/**
 * error handler that displays errors in a toast popup
 */
export default class ToastErrorHandler extends BasicErrorHandler
{
	protected toastService = new ToastService();

	constructor(rethrow: boolean = false, logLevel: LogLevel = LogLevel.ERROR)
	{
		super(rethrow, logLevel);
	}

	serviceError(response: any): void
	{
		if(response)
		{
			if (response.data?.error?.message)
			{
				this.toastService.errorToast(response.data.error.message);
			}
			else if (response instanceof BaseError)
			{
				this.toastService.errorToast(response.message);
			}
			else if (typeof response === "string")
			{
				this.toastService.errorToast(response);
			}
			else
			{
				this.toastService.errorToast("An unknown error has occurred. please contact support");
			}
		}
		else
		{
			this.toastService.errorToast("An unknown error has occurred. please contact support");
		}
	}
}