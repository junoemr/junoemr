
export default abstract class AbstractConverter<F, T>
{
	// ==========================================================================
	// Public Methods
	// ==========================================================================

	public abstract convert(from: F): T;

	public convertList(from: F[]): T[]
	{
		if (from)
		{
			return from.map((from) => this.convert(from));
		}
		return [];
	}

}