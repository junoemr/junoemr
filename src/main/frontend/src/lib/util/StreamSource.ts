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
	 * rewind the stream buy the specified amount
	 * @param amount - the number of items to rewind
	 */
	rewind(amount: number): void;

	/**
	 * fast forward the stream buy the specified amount
	 * @param amount - the number of items to fast forward
	 */
	fastForward(amount: number): void;

	/**
	 * Called by user to indicate to a source that it should preload stream content.
	 * This is for performance reasons only. There is not requirement to actually preload any thing.
	 */
	preload(): Promise<void>;

	// ==========================================================================
	// Getters
	// ==========================================================================

	/**
	 * the id of this source. implementation dependent.
	 */
	readonly sourceId: string;
}