import {StreamSource} from "../../../../util/StreamingList";
import Message from "../../../model/Message";
import MessagingServiceInterface, {MessageSearchParams} from "../../../service/MessagingServiceInterface";
import MessageSource from "../../../model/MessageSource";

export default class ClinicMailboxStreamSource implements StreamSource<Message>
{
	protected _messagingService: MessagingServiceInterface;
	protected _source: MessageSource;
	protected _searchParams: MessageSearchParams;
	protected _offset: number;
	protected readonly _bucketSize = 25;
	protected _bucket: Message[];
	protected _exhausted: boolean;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor(messagingService: MessagingServiceInterface, source: MessageSource, searchParams: MessageSearchParams)
	{
		this._messagingService = messagingService;
		this._source = source;
		this._searchParams = searchParams;
		this._offset = 0;
		this._bucket = [];
		this._exhausted = false;
	}

	// ==========================================================================
	// StreamSource Implementation
	// ==========================================================================

	public async peekNext(): Promise<Message>
	{
		await this.refillIfRequired();

		if (this._bucket.length > 0)
		{
			return this._bucket[this._bucket.length - 1];
		}
		return null;
	}

	public async popNext(): Promise<Message>
	{
		await this.refillIfRequired();

		if (this._bucket.length > 0)
		{
			return this._bucket.pop();
		}
		return null;
	}

	// ==========================================================================
	// Protected Methods
	// ==========================================================================

	protected async refillIfRequired(): Promise<void>
	{
		if (this._bucket.length <= 0 && !this._exhausted)
		{
			await this.refillBucket();
		}
	}

	protected async refillBucket(): Promise<void>
	{
		this._searchParams.offset = this._offset;
		this._searchParams.limit = this._bucketSize;

		this._bucket = (await this._messagingService.searchMessages(this._source, this._searchParams)).reverse();

		this._exhausted = this._bucket.length == 0;
		this._offset += this._bucketSize;
	}

}