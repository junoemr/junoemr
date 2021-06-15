import Integration from "../../model/Integration";
import {IntegrationType} from "../../model/IntegrationType";

export default class MhaIntegration implements Integration
{
	protected _apiKey: string;
	protected _id: string;
	protected _remoteId: string;
	protected _siteId: string;
	protected _siteName: string;
	protected _type: IntegrationType;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor(
		id: string,
		remoteId: string,
		type: IntegrationType,
		apiKey: string = null,
		siteId: string = null,
		siteName: string = null)
	{
		this._id = id;
		this._remoteId = remoteId;
		this._type = type;
		this._apiKey = apiKey;
		this._siteId = siteId;
		this._siteName = siteName;
	}

	/**
	 * check if integration enabled or not.
	 * @return always true. MHA integrations have no disable state.
	 */
	isEnabled(): boolean
	{
		// MHA integrations can't be disabled
		return true;
	}

	/**
	 * Check that the connection to the remote service represented by this integration is "working"
	 * @return promise that resolves to true / false indicating if the connection is valid.
	 */
	checkConnection(): Promise<boolean>
	{
		return Promise.resolve(false);
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	get apiKey(): string
	{
		return this._apiKey;
	}

	get id(): string
	{
		return this._id;
	}

	get remoteId(): string
	{
		return this._remoteId;
	}

	get siteId(): string
	{
		return this._siteId;
	}

	get siteName(): string
	{
		return this._siteName ? this._siteName : "No Site";
	}

	get type(): IntegrationType
	{
		return this._type;
	}
}