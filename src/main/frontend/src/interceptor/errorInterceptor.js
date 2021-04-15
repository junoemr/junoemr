/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

import {RestResponseError} from "../../generated";

angular.module('Interceptor').service('errorInterceptor', [
	'$q',
	'$injector',
	function ($q, $injector)
	{
		return {
			responseError: function (rejection)
			{
				// use injector to avoid circular dependency
				const $uibModal = $injector.get('$uibModal');
				const errorType = (rejection.data && rejection.data.error) ? rejection.data.error.type : null;

				// redirect to login page on 401 error.
				if (rejection.status === 401 && rejection.data === "<error>Not authorized</error>")
				{ // reload will cause server to redirect
					location.reload();
				}
				else if (rejection.status === 403 || errorType === RestResponseError.TypeEnum.SECURITY)
				{
					const defaultMessage = "You do not have the required permissions to access this data";
					const errorMessage = (rejection.data && rejection.data.error) ? rejection.data.error.message : null;

					console.error((errorMessage) ? errorMessage : defaultMessage, rejection);
				}

				return $q.reject(rejection);
			},
		};
	}
]);
