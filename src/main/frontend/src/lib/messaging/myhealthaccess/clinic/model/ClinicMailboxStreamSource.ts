import Message from "../../../model/Message";
import MessagingServiceInterface, {MessageSearchParams} from "../../../service/MessagingServiceInterface";
import MessageSource from "../../../model/MessageSource";
import {StreamSource} from "../../../../util/StreamSource";

export default class ClinicMailboxStreamSource implements StreamSource<Message>
{
	protected _messagingService: MessagingServiceInterface;
	protected _source: MessageSource;
	protected _searchParams: MessageSearchParams;
	protected _offset: number;
	protected readonly _bucketSize = 25;
	protected _bucket: Message[];
	protected _totalMessageCount: number = null;
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

	public rewind(amount: number): void
	{
		this._offset = Math.max(this._offset - (amount + this._bucket.length), 0);
		this._bucket = [];
	}

	public fastForward(amount: number): void
	{
		this._offset += (amount - this._bucket.length);
		this._bucket = [];
	}

	public async preload(): Promise<void>
	{
		await this.adjustForAsyncActivity();
		await this.refillIfRequired();
	}

	get sourceId(): string
	{
		return this._source.id;
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

		this.countMessages();// intentional no await
		this._bucket = (await this._messagingService.searchMessages(this._source, this._searchParams)).reverse();

		this._exhausted = this._bucket.length == 0;
		this._offset += this._bucket.length;
	}

	protected async countMessages(): Promise<void>
	{
		this._totalMessageCount = (await this._messagingService.countMessages(this._source, this._searchParams));
	}

	/**
	 * adjust this stream source to account for any async activities (other providers doing things) that may have
	 * occurred since the last message load. The stream will be fast forwarded or rewound adjust.
	 * @protected
	 */
	protected async adjustForAsyncActivity(): Promise<void>
	{
		if (this._totalMessageCount && !this._exhausted)
		{
			const currCount = (await this._messagingService.countMessages(this._source, this._searchParams));

			if (currCount > this._totalMessageCount)
			{
				this.fastForward(currCount - this._totalMessageCount);
			}
			else if (currCount < this._totalMessageCount)
			{
				this.rewind(this._totalMessageCount - currCount);
			}
		}
	}

}