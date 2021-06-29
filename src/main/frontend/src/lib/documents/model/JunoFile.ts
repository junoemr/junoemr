import {Moment} from "moment";

export default interface JunoFile
{
	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * get base 64 encoded data for this file.
	 * @return promise that resolves to base 64 encoded file data.
	 */
	getBase64Data(): Promise<string>;

	// ==========================================================================
	// Getters
	// ==========================================================================

	readonly name: string;
	readonly type: string;
	readonly createdAt: Moment;
	readonly updatedAt: Moment;

}