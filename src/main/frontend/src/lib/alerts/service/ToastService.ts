import {toastStore} from "../store/ToastStore";
import ToastFactory from "../factory/ToastFactory";
import Toast from "../model/Toast";

export default class ToastService
{
	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * show a notification toast to the user.
	 * @param message - notification text
	 * @param requireDismiss - [optional] [default false] if true message will not expire by its self. The user must click it to dismiss.
	 * @return the newly added toast
	 */
	public notificationToast(message: string, requireDismiss= false): Toast
	{
		const toast = ToastFactory.buildNotificationToast(message, requireDismiss);
		toastStore.addToast(toast);
		return toast;
	}

	/**
	 * show a success toast to the user.
	 * @param message - success text
	 * @param requireDismiss - [optional] [default false] if true message will not expire by its self. The user must click it to dismiss.
	 * @return the newly added toast
	 */
	public successToast(message: string, requireDismiss = false): Toast
	{
		const toast = ToastFactory.buildSuccessToast(message, requireDismiss);
		toastStore.addToast(toast);
		return toast;
	}

	/**
	 * show a warning toast to the user.
	 * @param message - warning text
	 * @param requireDismiss - [optional] [default false] if true message will not expire by its self. The user must click it to dismiss.
	 * @return the newly added toast
	 */
	public warningToast(message: string, requireDismiss = false): Toast
	{
		const toast = ToastFactory.buildWarningToast(message, requireDismiss);
		toastStore.addToast(toast);
		return toast;
	}

	/**
	 * show a error toast to the user.
	 * @param message - error text
	 * @param requireDismiss - [optional] [default true] if true message will not expire by its self. The user must click it to dismiss.
	 * @return the newly added toast
	 */
	public errorToast(message: string, requireDismiss = true): Toast
	{
		const toast = ToastFactory.buildErrorToast(message, requireDismiss);
		toastStore.addToast(toast);
		return toast;
	}


	/**
	 * dismiss a currently displayed toast.
	 * @param toast - the toast to dismiss
	 */
	public dismissToast(toast: Toast): void
	{
		toastStore.dismissToast(toast);
	}
}