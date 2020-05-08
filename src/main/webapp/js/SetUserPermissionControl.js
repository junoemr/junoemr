"use strict"

var JS = JS || {};

if (!JS.SetUserPermissionControl) {
	JS.SetUserPermissionControl = {};
}


JS.SetUserPermissionControl.checkProviderPermission = function checkProviderPermission (isLoginUserSuperAdmin, isProviderSuperAdmin, unauthorizedUserMSG)
{
	console.log(typeof isLoginUserSuperAdmin);
	console.log(typeof isProviderSuperAdmin);
	if (!isLoginUserSuperAdmin && isProviderSuperAdmin)
	{
		JS.SetUserPermissionControl.showUnauthorizedUserMSG(unauthorizedUserMSG);
		return false;
	}
	return true;
};

JS.SetUserPermissionControl.showUnauthorizedUserMSG = function showUnauthorizedUserMSG(unauthorizedUserMSG)
{
	alert(unauthorizedUserMSG);
};