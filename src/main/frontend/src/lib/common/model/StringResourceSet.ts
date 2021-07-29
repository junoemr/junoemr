
export default interface StringResourceSet
{
	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * check if the specified key is present in this resource set.
	 * @param key - the key to look for Ex. "foo.bar"
	 * @return true / false
	 */
	hasKey(key: string): boolean;

	/**
	 * get a string from this resource set
	 * @param key - the key to get
	 * @return the string
	 * @throws NoSuchResourceError if the string with specified key is not found.
	 */
	getString(key: string): string;
}