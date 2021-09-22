/**
 * simple queue object for handling a loading state across multiple async requests
 */
export default class LoadingQueue
{
	private loadingList: boolean[] = [];

	public pushLoadingState(): void
	{
		this.loadingList.push(true);
	}

	public popLoadingState(): void
	{
		this.loadingList.pop();
	}

	get isLoading(): boolean
	{
		return this.loadingList.length > 0;
	}
}