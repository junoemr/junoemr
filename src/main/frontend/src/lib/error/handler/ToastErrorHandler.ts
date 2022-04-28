import ToastService from "../../alerts/service/ToastService";
import BasicErrorHandler from "./BasicErrorHandler";

/**
 * error handler that displays errors in a toast popup
 */
export default class ToastErrorHandler extends BasicErrorHandler
{
	protected toastService = new ToastService();

	serviceError(response: any): void
	{
		if(response?.data?.error?.message)
		{
			this.toastService.errorToast(response.data.error.message);
		}
		else
		{
			this.toastService.errorToast("An unknown error has occurred. please contact support");
		}
	}
}