// appointment booking information returned from bookAppointmentModal
export default class AppointmentBooking
{
	public static DEFAULT_BOOKING_DURATION = "15";

	public demographic = null;
	public notes: string = "";
	public reason: string = "";
	public duration: string = "";
	public reasonType: string = null;
	public appointmentType: string = null;
	public critical: boolean = false;
	public virtual: boolean = false;
	public siteId: string = null;

	// @ts-ignore
	get demographicNo(): string
	{
		if (this.demographic)
		{
			return this.demographic.demographicNo;
		}

		return null;
	}
}
