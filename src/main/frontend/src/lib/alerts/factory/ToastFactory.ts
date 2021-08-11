import BasicToast from "../model/BasicToast";
import DismissToast from "../model/DismissToast";
import Toast from "../model/Toast";
import {ToastStyle} from "../model/ToastStyle";

export default class ToastFactory
{
	// ==========================================================================
	// Class Methods
	// ==========================================================================

	/**
	 * build a notification toast
	 * @param message - message to display
	 * @param requireDismiss - if ture notification will not auto dismiss. Requires user to click to dismiss.
	 */
	public static buildNotificationToast(message: string, requireDismiss= false): Toast
	{
		return this.buildToast(message, ToastStyle.Notification, requireDismiss, "icon-exclamation");
	}

	/**
	 * build a success toast
	 * @param message - message to display
	 * @param requireDismiss - if ture notification will not auto dismiss. Requires user to click to dismiss.
	 */
	public static buildSuccessToast(message: string, requireDismiss= false): Toast
	{
		return this.buildToast(message, ToastStyle.Success, requireDismiss, "icon-check");
	}

	/**
	 * build a warning toast
	 * @param message - message to display
	 * @param requireDismiss - if ture notification will not auto dismiss. Requires user to click to dismiss.
	 */
	public static buildWarningToast(message: string, requireDismiss= false): Toast
	{
		return this.buildToast(message, ToastStyle.Warning, requireDismiss, "icon-exclamation");
	}

	/**
	 * build a error toast
	 * @param message - message to display
	 * @param requireDismiss - [default true] if ture notification will not auto dismiss. Requires user to click to dismiss.
	 */
	public static buildErrorToast(message: string, requireDismiss= true): Toast
	{
		return this.buildToast(message, ToastStyle.Error, requireDismiss, "icon-cancel");
	}

	/**
	 * build a new toast
	 * @param message - message to display
	 * @param style - the style of the toast
	 * @param requireDismiss - if ture notification will not auto dismiss. Requires user to click to dismiss.
	 * @param icon - icon to show
	 */
	public static buildToast(message: string, style: ToastStyle, requireDismiss, icon)
	{
		if (requireDismiss)
		{
			return new DismissToast(message, style, icon);
		}
		else
		{
			return new BasicToast(message, style, icon);
		}
	}
}