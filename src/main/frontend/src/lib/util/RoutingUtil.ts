import {MhaClinicMessagingApi} from "../../../generated";
import {API_BASE_PATH} from "../constants/ApiConstants";

export default class RoutingUtil
{
	// ==========================================================================
	// Public Class Methods
	// ==========================================================================

	/**
	 * just like $state.go(...) but opens in a new tab!
	 * @param $state - ui router $state provider
	 * @param state - the state to transition to
	 * @param props - the properties to bind to that state (must be url properties).
	 */
	public static goNewTab($state: any, state: string, props: any): void
	{
		const url = $state.href(state, props);
		window.open(url, "_blank");
	}
}