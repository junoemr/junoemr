
export default interface Toast
{
	visible: boolean;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * called when the user clicks the toast (if clickable)
	 */
	onClick(): void;

	// ==========================================================================
	// Getters
	// ==========================================================================

	readonly message: string; // message to display in the toast
	readonly icon: string; // icon class to use for the toast
	readonly cssClasses: string[]; // css class to apply to the toast
	readonly clickable: boolean; // true / false if the toast can be clicked
	readonly durationMs: number; // duration of the toast in milliseconds (unless click to dismiss)
	readonly unlimitedDuration: boolean; // if true durationMs is ignored.
}