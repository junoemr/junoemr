import BaseError from "./BaseError";
import {SecurityPermissions} from "../../common/security/securityConstants";

export default class SecurityError extends BaseError
{
	constructor(permission: SecurityPermissions)
	{
		super("Missing Security Role Permission: " + permission);
	}
}