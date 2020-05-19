"use strict"


var Juno = window.Juno || {};
Juno.Admin = Juno.Admin || {};
Juno.Admin.UserPermissionCtrl = Juno.Admin.UserPermissionCtrl || {};

if (!Juno.Admin.UserPermissionCtrl)
{
	Juno.Admin.UserPermissionCtrl = {};
}

Juno.Admin.UserPermissionCtrl.checkProviderPermission = function checkProviderPermission (isLoginUserSuperAdmin, isProviderSuperAdmin)
{
	if (!isLoginUserSuperAdmin && isProviderSuperAdmin)
	{
		return false;
	}
	return true;
};
