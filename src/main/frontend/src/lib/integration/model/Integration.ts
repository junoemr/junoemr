import {IntegrationType} from "./IntegrationType";

export default interface Integration
{
	// ==========================================================================
	// Getters
	// ==========================================================================

	readonly id: string;
	readonly remoteId: string;
	readonly apiKey: string;
	readonly siteId: string;
	readonly siteName: string;
	readonly type: IntegrationType;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * check if integration enabled or not.
	 * @return true / false indicating if this integration is enabled or not.
	 */
	isEnabled(): boolean;

	/**
	 * Check that the connection to the remote service represented by this integration is "working"
	 * @return promise that resolves to true / false indicating if the connection is valid.
	 */
	checkConnection(): Promise<boolean>;
}