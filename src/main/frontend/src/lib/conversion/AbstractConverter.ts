import {Moment} from "moment";

export default abstract class AbstractConverter<F, T>
{
	// ==========================================================================
	// Public Methods
	// ==========================================================================

	public abstract convert(from: F, ...args: any): T;

	public convertList(from: F[], ...args: any): T[]
	{
		if (from)
		{
			return from.map((from) => this.convert(from, ...args));
		}
		return [];
	}

	// ==========================================================================
	// Common Conversion Methods
	// ==========================================================================

	protected serializeLocalDateTime(moment: Moment): string
	{
		if(moment && moment.isValid())
		{
			return Juno.Common.Util.formatMomentDateTimeNoTimezone(moment);
		}
		return null;
	}

	protected serializeZonedDateTime(moment: Moment): string
	{
		if(moment && moment.isValid())
		{
			return Juno.Common.Util.formatZonedMomentDateTime(moment);
		}
		return null;
	}

}