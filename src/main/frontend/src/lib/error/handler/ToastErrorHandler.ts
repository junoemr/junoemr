import {ErrorHandler} from "./ErrorHandler";
import ToastService from "../../alerts/service/ToastService";

/**
 * error handler that displays errors in a toast popup
 */
export default class ToastErrorHandler implements ErrorHandler
{
	protected toastService = new ToastService();

	public handleError(response: any): any
	{
		console.error(response);
		if(response && response.data && response.data.error)
		{
			if(response.status >= 500) // 500 series errors
			{
				this.toastService.errorToast("An unknown error has occurred. please contact support");
			}
			else
			{
				this.toastService.errorToast(response.data.error.message);
			}
			throw response.data.error;
		}
		else
		{
			this.toastService.errorToast("An unknown error has occurred. please contact support");
			throw response;
		}
	}
}