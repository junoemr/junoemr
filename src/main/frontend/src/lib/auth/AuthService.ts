import {netcareService} from "../integration/netcare/service/NetcareService";

export default class AuthService
{
	constructor()
	{
	}

	public logout(): void
	{
		try
		{
			if(netcareService.isLoggedIn())
			{
				netcareService.submitLogoutForm();
			}
		}
		// just in case, make sure this happens regardless
		finally
		{
			// can eventually be replaced with rest call
			window.location.href = "../logout.jsp";
		}
	}
}

// service is meant to be a singleton
export const authService = new AuthService();