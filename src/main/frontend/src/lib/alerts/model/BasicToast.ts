import Toast from "./Toast";
import {ToastStyle} from "./ToastStyle";
import ToastService from "../service/ToastService";

export default class BasicToast implements Toast
{
	public static readonly TOAST_DEFAULT_DURATION = 5000;

	protected _visible: boolean = true;
	protected _durationMs: number;
	protected _icon: string;
	protected _message: string;
	protected _style: ToastStyle;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * build a new basic toast
	 * @param message - message to display
	 * @param style - [optional] [default Notification] the style of this toast
	 * @param icon - [optional] icon to use
	 * @param duration - [optional] [default 5 seconds] duration of the toast
	 */
	constructor(message: string, style: ToastStyle = ToastStyle.Notification, icon: string = null, duration = BasicToast.TOAST_DEFAULT_DURATION)
	{
		this._message = message;
		this._style = style;
		this._icon = icon;
		this._durationMs = duration;
	}

	onClick(): void
	{
		(new ToastService()).dismissToast(this);
	}

	// ==========================================================================
	// Setters
	// ==========================================================================

	set visible(visible: boolean)
	{
		this._visible = visible;
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	get visible(): boolean
	{
		return this._visible;
	}

	get clickable(): boolean
	{
		return true;
	}

	get cssClasses(): string[]
	{
		return [this._style];
	}

	get durationMs(): number
	{
		return this._durationMs;
	}

	get icon(): string
	{
		return this._icon;
	}

	get message(): string
	{
		return this._message;
	}

	get unlimitedDuration(): boolean
	{
		return false;
	}
}