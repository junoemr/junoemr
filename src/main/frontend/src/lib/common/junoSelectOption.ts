/**
 * An interface that represents a selectable option for the Juno Select component.
 * A component that uses junoSelect.jsp should pass in a list of objects that implement this interface.
 */
export interface JunoSelectOption {
	value: any;
	// Visible string representing the option
	label: string;
	data?: any;
}