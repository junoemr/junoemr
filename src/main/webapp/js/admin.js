var unauthorizedUserMSG = "You are not authorized to change this user setting!";

	function adminUpdateProviderSetting (isLoginUserSuperAdmin, isProviderSuperAdmin)
{
	console.log("user S admin: "+isLoginUserSuperAdmin+", provider S admin: "+isProviderSuperAdmin);
	if (!isLoginUserSuperAdmin && isProviderSuperAdmin)
	{
		showUnauthorizedUserMSG();
		return false;
	}
	return true;
}

function showUnauthorizedUserMSG()
{
	alert(unauthorizedUserMSG);
}