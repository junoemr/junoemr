
export enum VirtualAppointmentType {
	None = "NONE",
	Video = "VIDEO",
	Audio = "AUDIO",
	Chat = "CHAT",
}

/**
 * get the list of virtual appointment types as a option list (for use in drop downs).
 * @return virtual appointments types as option list.
 */
export function virtualAppointmentTypeOptions(): {label: string, value: VirtualAppointmentType}[]
{
	return [
		{label: "Non Virtual", value: VirtualAppointmentType.None},
		{label: "Telehealth", value: VirtualAppointmentType.Video},
		{label: "Chat", value: VirtualAppointmentType.Chat},
		// audio intentionally left out.
	];
}