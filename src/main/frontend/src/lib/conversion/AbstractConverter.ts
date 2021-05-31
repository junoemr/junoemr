
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

}