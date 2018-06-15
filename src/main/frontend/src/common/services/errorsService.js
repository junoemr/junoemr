'use strict'


angular.module('Common.Services').factory(
	'errorsService',

	[

		function()
		{
			var messages = {};

			/*************************************************************/
			// Factory
			// @param {errors_hash} Error hash from Clinicaid API
			// @param {options} Hash
			//		- {display_field_errors} Boolean : Sets flag
			//		- {field_errors_label_map} Hash : Can pass a hash in to get field
			// 		labels for displaying field errors. Assumed to be field => label
			//		- {field_errors_label_map_key} String : Allows for
			// 		field => Hash[key] lookup for label.
			/*************************************************************/
			messages.factory = function factory(errors_hash, options)
			{
				return new Juno.Common.DisplayMessages(errors_hash, options);
			};

			return messages;
		}]
);