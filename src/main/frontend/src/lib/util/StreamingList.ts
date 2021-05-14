
export default class StreamingList<T> extends Array<T>
{
	protected _sources: StreamSource<T>[];
	protected _comparator: (t1: T, t2: T) => number;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * build streaming list
	 * @param sources - stream sources for this list
	 * @param comparator - a function used to compare elements from different sources. Operates the same as array sort compareFunction
	 */
	constructor(sources: StreamSource<T>[], comparator: (t1: T, t2: T) => number) {
		super();
		this._sources = sources;
		this._comparator = comparator;
	}

	/**
	 * load the next, amount items from the stream in to the list.
	 * @param amount - the amount of items to load.
	 * @return - the actual number of items loaded (can be lower if at end of list).
	 */
	public async load(amount: number): Promise<number>
	{
		let loaded = 0;
		for(let i = 0; i < amount; i++)
		{
			const next = await this.getNext();
			if (next)
			{
				this.push(next);
				loaded++;
			}
		}

		return loaded;
	}

	// ==========================================================================
	// Protected Methods
	// ==========================================================================

	/**
	 * get next item from stream
	 * @return - the next item in the stream or null if stream exhausted
	 * @protected
	 */
	protected async getNext(): Promise<T>
	{
		let nextSource: StreamSource<T> = null;
		for(let source of this._sources)
		{
			if (await source.peekNext() != null)
			{
				if (nextSource === null)
				{
					nextSource = source;
				}
				else if (this._comparator(await nextSource.peekNext(), await source.peekNext()) > 0 )
				{
					nextSource = source;
				}
			}
		}

		if (nextSource !== null)
		{
			return await nextSource.popNext();
		}
		return null;
	}

}

export interface StreamSource<T>
{
	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * get the next item from this source
	 * @return the next item or null if no more items.
	 */
	peekNext(): Promise<T>;

	/**
	 * get the next item from this source, poping it from the stack
	 * @return the next item or null if no more items.
	 */
	popNext(): Promise<T>;
}