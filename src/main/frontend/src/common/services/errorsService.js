'use strict'


angular.module('Common.Services').factory(
	'errorsService',

	[

		function()
		{
			var messages = {};

			/*************************************************************/
			// Factory
			// @param {errorsHash} Error hash from Clinicaid API
			// @param {options} Hash
			//		- {displayFieldErrors} Boolean : Sets flag
			//		- {fieldErrorsLabelMap} Hash : Can pass a hash in to get field
			// 		labels for displaying field errors. Assumed to be field => label
			//		- {fieldErrorsLabelMapKey} String : Allows for
			// 		field => Hash[key] lookup for label.
			/*************************************************************/
			messages.factory = function factory(errorsHash, options)
			{
				return new Juno.Common.DisplayMessages(errorsHash, options);
			};

			return messages;
		}]
);