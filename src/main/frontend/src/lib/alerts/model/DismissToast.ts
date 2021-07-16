import BasicToast from "./BasicToast";
import ToastService from "../service/ToastService";
import {ToastStyle} from "./ToastStyle";

// like basic toast but never expires. The user must click to dismiss
export default class DismissToast extends BasicToast
{
	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * build a new dismiss toast
	 * @param message - message to display
	 * @param style - [optional] [default Notification] the style of this toast
	 * @param icon - [optional] icon to use
	 * @param duration - [optional] [default 5 seconds] duration of the toast
	 */
	constructor(message: string, style: ToastStyle = ToastStyle.Notification, icon: string = null, duration = BasicToast.TOAST_DEFAULT_DURATION)
	{
		super(message, style, icon, duration);
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	get unlimitedDuration(): boolean
	{
		return true;
	}
}