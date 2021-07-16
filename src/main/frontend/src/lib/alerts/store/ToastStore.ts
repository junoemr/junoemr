import Toast from "../model/Toast";
import {IScope} from "angular";

class ToastStore
{
	protected readonly FADE_OUT_TIME_MS = 1000;// 1 seconds
	protected _activeToasts: Toast[] = [];

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * add a toast to the active toast list (display it)
	 * @param toast
	 */
	public addToast(toast: Toast)
	{
		this._activeToasts.push(toast);

		if (!toast.unlimitedDuration)
		{
			window.setTimeout(() => this.fadeOutToast(toast), toast.durationMs);
		}

		this.applyUpdates();
	}

	/**
	 * dismiss a toast that is currently being displayed
	 * @param toast - the toast to dismiss
	 */
	public dismissToast(toast: Toast): void
	{
		this.fadeOutToast(toast);
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	get activeToasts(): Toast[]
	{
		return this._activeToasts;
	}

	// ==========================================================================
	// Protected Methods
	// ==========================================================================

	/**
	 * fade out a toast then delete it
	 * @param toast - the toast to fade then delete
	 * @protected
	 */
	protected fadeOutToast(toast: Toast): void
	{
		toast.visible = false;
		window.setTimeout(() => this.deleteToast(toast), this.FADE_OUT_TIME_MS);

		this.applyUpdates();
	}

	/**
	 * delete an active toast
	 * @param toast - the toast to delete
	 * @protected
	 */
	protected deleteToast(toast: Toast): void
	{
		const index = this._activeToasts.indexOf(toast);
		if (index != -1)
		{
			this._activeToasts.splice(index, 1);
		}

		this.applyUpdates();
	}

	/**
	 * grab the root angular scope
	 * @return rootScope
	 * @protected
	 */
	protected getRootScope(): IScope
	{
		return angular.element("body").scope();
	}

	/**
	 * notify angular UI of changes
	 * @protected
	 */
	protected applyUpdates(): void
	{
		if (!this.getRootScope().$$phase)
		{
			this.getRootScope().$apply();
		}
	}
}

// singleton export
export const toastStore = new ToastStore();