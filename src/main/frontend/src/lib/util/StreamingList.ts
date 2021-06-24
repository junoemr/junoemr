import ActionAlreadyInProgressError from "../error/ActionAlreadyInProgressError";

export default class StreamingList<T> extends Array<T>
{
	protected _sources: StreamSource<T>[];
	protected _comparator: (t1: T, t2: T) => number;
	protected _isLoading = false;

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
	 * @throws ActionAlreadyInProgressError if another load operation is already under way.
	 */
	public async load(amount: number): Promise<number>
	{
		let loaded = 0;

		if (this._isLoading)
		{
			throw new ActionAlreadyInProgressError("Streaming List is currently loading new items. Please wait for it to complete")
		}

		try
		{
			this._isLoading = true;

			await this.preloadSources();
			for (let i = 0; i < amount; i++)
			{
				const next = await this.getNext();
				if (next)
				{
					this.push(next);
					loaded++;
				}
			}
		}
		finally
		{
			this._isLoading = false;
		}

		return loaded;
	}

	/**
	 * remove an item from the list.
	 * @param item - item to remove. Will only remove first occurrence. No effect if not found.
	 */
	public remove(item: T): void
	{
		if (this.indexOf(item) !== -1)
		{
			this.splice(this.indexOf(item), 1);
		}
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	get isLoading(): boolean
	{
		return this._isLoading;
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
		for(const source of this._sources)
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

	protected async preloadSources(): Promise<void>
	{
		await Promise.all(this._sources.map((source) => source.preload()));
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
	
	/**
	 * Called by user to indicate to a source that it should preload stream content.
	 * This is for performance reasons only. There is not requirement to actually preload any thing.
	 */
	preload(): Promise<void>;
}