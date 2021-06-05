
export enum LinkStatus
{
	NO_LINK = "NO_LINK",
	PATIENT_REJECTED = "PATIENT_REJECTED",
	CLINIC_REJECTED = "CLINIC_REJECTED",
	PENDING_PATIENT_CONFIRM = "PENDING_PATIENT_CONFIRM",
	PENDING_CLINIC_CONFIRM = "PENDING_CLINIC_CONFIRM",
	CONFIRMED = "CONFIRMED",
	VERIFIED = "VERIFIED",
}

/**
 * convert a link status enum value to number which represents the order of the status.
 * @param linkStatus - the link status to get the order number for.
 * @return a number representing the order of the status. where 0 is NO_LINK, 1 is PATIENT_REJECTED and so on.
 */
export function linkStatusToVerificationLevel(linkStatus: LinkStatus): number
{
	switch (linkStatus)
	{
		case LinkStatus.NO_LINK:
			return 0;
		case LinkStatus.PATIENT_REJECTED:
			return 1;
		case LinkStatus.CLINIC_REJECTED:
			return 2;
		case LinkStatus.PENDING_PATIENT_CONFIRM:
			return 3;
		case LinkStatus.PENDING_CLINIC_CONFIRM:
			return 4;
		case LinkStatus.CONFIRMED:
			return 5;
		case LinkStatus.VERIFIED:
			return 6;
		default:
			throw Error(`Counld not convert link status to number. Link status [${linkStatus}] is not a know enum value`);
	}
}