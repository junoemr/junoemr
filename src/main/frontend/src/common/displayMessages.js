'use strict'


window.Juno = window.Juno || {};
window.Juno.Common = window.Juno.Common || {};

window.Juno.Common.DisplayMessages = function DisplayMessages(messages_hash, options)
{
	var me = this;

	/*************************************************************/
	// Local Variables
	/*************************************************************/

	me.messages_hash = {};
	me.display_field_errors = true;
	me.field_errors_label_map = {};
	me.field_errors_label_map_key = null;
	me.extra_info = {};
	me.meta = {};

	me.saving_message = 'Saving...';

	/*************************************************************/
	// Initialize
	// @param {messages_hash} Error hash from Juno API
	// @param {options} Hash
	//		- {display_field_errors} Boolean : Sets flag
	//		- {field_errors_label_map} Hash : Can pass a hash in to get field
	// 		labels for displaying field errors. Assumed to be field => label
	//		- {field_errors_label_map_key} String : Allows for
	// 		field => Hash[key] lookup for label.
	/*************************************************************/

	me.init = function init()
	{
		if (Juno.Common.Util.exists(messages_hash))
		{
			me.messages_hash = messages_hash;
		}

		// Datamapper field errors are inconsistent and can return either a
		// hash -> array or hash -> string
		me.force_field_error_arrays();

		// Parse options
		if (Juno.Common.Util.exists(options))
		{
			if (Juno.Common.Util.exists(options.display_field_errors) && options.display_field_errors === false)
			{
				me.display_field_errors = false;
			}
			if (Juno.Common.Util.exists(options.field_errors_label_map))
			{
				me.field_errors_label_map = options.field_errors_label_map;
				if (Juno.Common.Util.exists(options.field_errors_label_map_key))
				{
					me.field_errors_label_map_key = options.field_errors_label_map_key;
				}
			}
		}
	};

	// merge another display messages object into this one
	me.merge = function merge(display_messages)
	{
		// does not merge extra_info or meta - just messages

		me.messages_hash = angular.merge(me.messages_hash, display_messages.messages_hash);
	};

	/*************************************************************/
	// Alert Warnings
	/*************************************************************/

	me.has_alert_warnings = function has_alert_warnings()
	{
		return me.alert_warnings().length > 0;
	};

	me.alert_warnings = function alert_warnings()
	{
		if (Juno.Common.Util.exists(me.messages_hash) &&
				Juno.Common.Util.exists(me.messages_hash.alert_warnings))
		{
			return me.messages_hash.alert_warnings;
		}
		return [];
	};

	me.add_alert_warning = function add_alert_warning(message)
	{
		if (!me.has_alert_warnings())
		{
			me.messages_hash.alert_warnings = [];
		}
		me.messages_hash.alert_warnings.push(message);
	};

	/*************************************************************/
	// Standard Errors
	// Generic errors not tied to any specific element.
	/*************************************************************/

	// Returns true if there are any field errors
	me.has_validation_warnings = function has_validation_warnings()
	{
		return me.validation_warnings().length > 0;
	};

	me.add_validation_warning = function add_validation_warning(message)
	{
		if (!me.has_validation_warnings())
		{
			me.messages_hash.validation_warnings = [];
		}
		me.messages_hash.validation_warnings.push(message);
	};

	me.validation_warnings = function validation_warnings()
	{
		if (Juno.Common.Util.exists(me.messages_hash) &&
				Juno.Common.Util.exists(me.messages_hash.validation_warnings))
		{
			return me.messages_hash.validation_warnings;
		}
		return [];
	};

	/*************************************************************/
	// Standard Errors
	// Generic errors not tied to any specific element.
	/*************************************************************/

	me.has_standard_errors = function has_standard_errors()
	{
		return me.standard_errors().length > 0;
	};

	me.standard_errors = function standard_errors()
	{
		if (Juno.Common.Util.exists(me.messages_hash) &&
				Juno.Common.Util.exists(me.messages_hash.standard_errors))
		{
			return me.messages_hash.standard_errors;
		}
		return [];
	};

	me.add_standard_error = function add_standard_error(message)
	{
		if (!me.has_standard_errors())
		{
			me.messages_hash.standard_errors = [];
		}
		me.messages_hash.standard_errors.push(message);
	};

	me.add_generic_fatal_error = function add_generic_fatal_error()
	{
		// TODO-legacy: change this
		me.add_standard_error("Fatal Error");
		//		Juno.Context.Settings.interface_preferences.generic_fatal_error_message);
	};

	/*************************************************************/
	// Standard Warnings
	// Generic warnings not tied to any specific element.
	/*************************************************************/

	me.has_standard_warnings = function has_standard_warnings()
	{
		return me.standard_warnings().length > 0;
	};

	me.standard_warnings = function standard_warnings()
	{
		if (Juno.Common.Util.exists(me.messages_hash) &&
				Juno.Common.Util.exists(me.messages_hash.standard_warnings))
		{
			return me.messages_hash.standard_warnings;
		}
		return [];
	};

	me.add_standard_warning = function add_standard_warning(message)
	{
		if (!me.has_standard_warnings())
		{
			me.messages_hash.standard_warnings = [];
		}
		me.messages_hash.standard_warnings.push(message);
	};

	/*************************************************************/
	// Standard Info Message
	// Generic info message not tied to any specific element.
	/*************************************************************/

	me.has_standard_info = function has_standard_info()
	{
		return me.standard_info().length > 0;
	};

	me.standard_info = function standard_info()
	{
		if (Juno.Common.Util.exists(me.messages_hash) &&
				Juno.Common.Util.exists(me.messages_hash.standard_info))
		{
			return me.messages_hash.standard_info;
		}
		return [];
	};

	me.add_standard_info = function add_standard_info(message)
	{
		if (!me.has_standard_info())
		{
			me.messages_hash.standard_info = [];
		}
		me.messages_hash.standard_info.push(message);
	};

	/*************************************************************/
	// Standard Success Message
	// Generic success message not tied to any specific element.
	/*************************************************************/

	me.has_standard_success = function has_standard_success()
	{
		return me.standard_success().length > 0;
	};

	me.standard_success = function standard_success()
	{
		if (Juno.Common.Util.exists(me.messages_hash) &&
				Juno.Common.Util.exists(me.messages_hash.standard_success))
		{
			return me.messages_hash.standard_success;
		}
		return [];
	};

	me.add_standard_success = function add_standard_success(message)
	{
		if (!me.has_standard_success())
		{
			me.messages_hash.standard_success = [];
		}
		me.messages_hash.standard_success.push(message);
	};

	/*************************************************************/
	// Field Errors
	// These errors are directly related to form fields.
	/*************************************************************/

	// Returns true if there are any field errors
	me.has_field_errors = function has_field_errors()
	{
		return me.field_errors() && !angular.equals(me.field_errors(), {});
	};

	// Returns a hash of field errors
	me.field_errors = function field_errors(prepend_label)
	{
		var field_errors = {};
		if (Juno.Common.Util.exists(me.messages_hash) &&
				Juno.Common.Util.exists(me.messages_hash.field_errors))
		{
			if (Juno.Common.Util.exists(prepend_label) && prepend_label === true)
			{
				for (var key in me.messages_hash.field_errors)
				{
					field_errors[key] = me.prepend_label(
							key, me.messages_hash.field_errors[key]);
				}
			}
			else
			{
				field_errors = me.messages_hash.field_errors;
			}
		}
		return field_errors;
	};

	// Returns true if there is a field error for any of the passed in keys
	me.has_one_of_field_error = function has_one_of_field_error(keys)
	{
		if (!angular.isArray(keys))
		{
			keys = [keys];
		}

		for (var i in keys)
		{
			if (me.has_field_error(keys[i]))
			{
				return true;
			}
		}
		return false;
	};

	// Returns true if this field error exists and this error object has the matching index.
	// Used for invoice multi post which posts multiple invoices line items, and returns an index
	// corresponding to the line item with the error
	me.has_indexed_field_error = function has_indexed_field_error(key, index)
	{
		if(Juno.Common.Util.exists(me.extra_info) &&
				Juno.Common.Util.exists(me.extra_info.index) &&
				me.extra_info.index == index)
		{
			return me.has_field_error(key);
		}
		return false;
	};

	// Returns true if this field error exists
	me.has_field_error = function has_field_error(key)
	{
		if (me.has_field_errors())
		{
			if (key in me.field_errors())
			{
				return true;
			}
		}
		return false;
	};

	// Converts all field errors to strings. Concatinates arrays. Useful for our ca-field
	// directives that use ca-error as it doesn't accept a boolean value.
	me.field_error_string = function field_error_string(key, concat_string)
	{
		if (me.has_field_error(key))
		{
			if (!Juno.Common.Util.exists(concat_string))
			{
				concat_string = ", ";
			}
			return me.field_errors()[key].join(concat_string);
		}
		return "";
	};

	// Adds a new field error with the given message
	me.add_field_error = function add_field_error(key, message)
	{
		if (!me.has_field_errors())
		{
			me.messages_hash.field_errors = {};
		}
		if (!me.has_field_error(key))
		{
			me.messages_hash.field_errors[key] = [];
		}
		me.messages_hash.field_errors[key].push(message)
	};

	// Removes a field error.
	me.remove_field_error = function remove_field_error(key)
	{
		if(me.has_field_error(key))
		{
			delete me.messages_hash.field_errors[key];
		}
	};

	/*************************************************************/
	// Error links
	// These are error messages that have an embeded link in them
	// Eg: This patient already exists. Press "here" to view patient.
	/*************************************************************/

	me.has_error_links = function has_error_links()
	{
		return me.error_links().length > 0;
	};

	me.add_error_link = function add_error_link(message)
	{
		if (!me.has_error_links())
		{
			me.messages_hash.error_links = [];
		}
		me.messages_hash.error_links.push(message);
	};

	me.error_links = function error_links()
	{
		if (Juno.Common.Util.exists(me.messages_hash) &&
				Juno.Common.Util.exists(me.messages_hash.error_links))
		{
			return me.messages_hash.error_links;
		}
		return [];
	};

	/*************************************************************/
	// Field Warnings
	// These warnings are directly related to form fields.
	/*************************************************************/

	// Returns true if there are any field warnings
	me.has_field_warnings = function has_field_warnings()
	{
		return me.field_warnings() && !angular.equals(me.field_warnings(), {});
	};

	// Returns a hash of field warning
	me.field_warnings = function field_warnings(prepend_label)
	{
		var field_warnings = {};
		if (Juno.Common.Util.exists(me.messages_hash) &&
			Juno.Common.Util.exists(me.messages_hash.field_warnings))
		{
			if (Juno.Common.Util.exists(prepend_label) && prepend_label === true)
			{
				for (var key in me.messages_hash.field_warnings)
				{
					field_warnings[key] = me.prepend_label(
						key, me.messages_hash.field_warnings[key]);
				}
			}
			else
			{
				field_warnings = me.messages_hash.field_warnings;
			}
		}
		return field_warnings;
	};
	// Returns true if this field warning exists
	me.has_field_warning = function has_field_warning(key)
	{
		if (me.has_field_warnings())
		{
			if (key in me.field_warnings())
			{
				return true;
			}
		}
		return false;
	};

	// Converts all field warnings to strings. Concatinates arrays. Useful for our ca-field
	// directives that use ca-warning as it doesn't accept a boolean value.
	me.field_warning_string = function field_warning_string(key, concat_string)
	{
		if (me.has_field_warning(key))
		{
			if (!Juno.Common.Util.exists(concat_string))
			{
				concat_string = ", ";
			}
			return me.field_warnings()[key].join(concat_string);
		}
		return "";
	};

	// Adds a new field warning with the given message
	me.add_field_warning = function add_field_warning(key, message)
	{
		if (!me.has_field_warnings())
		{
			me.messages_hash.field_warnings = {};
		}
		if (!me.has_field_warning(key))
		{
			me.messages_hash.field_warnings[key] = [];
		}
		me.messages_hash.field_warnings[key].push(message)
	};

	// Removes a field error.
	me.remove_field_warning = function remove_field_warning(key)
	{
		if(me.has_field_warning(key))
		{
			delete me.messages_hash.field_warnings[key];
		}
	};

	/*************************************************************/
	// Helpers
	/*************************************************************/

	me.has_errors = function has_errors()
	{
		if (me.has_field_errors() ||
				me.has_standard_errors() ||
				me.has_error_links())
		{
			return true;
		}
		return false;
	};

	me.clear = function clear()
	{
		me.messages_hash = {};
	};

	// Make sure each field error value is an array of messages even if there
	// is only one element.
	me.force_field_error_arrays = function force_field_error_arrays()
	{
		if (me.has_field_errors())
		{
			for (var i in me.messages_hash.field_errors)
			{
				if (!angular.isArray(me.messages_hash.field_errors[i]))
				{
					me.messages_hash.field_errors[i] = [
						angular.copy(me.messages_hash.field_errors[i])
					];
				}
			}
		}
	};

	// Prepend field label to all field error messages
	me.prepend_label = function prepend_label(field, field_errors)
	{
		var label = "";
		var out_field_errors = [];
		if (field in me.field_errors_label_map)
		{
			// Lookup label in basic key -> label map
			label = me.field_errors_label_map[field];

			// If label map is a hash of hashes, grab the label from the
			// provided label field
			if (Juno.Common.Util.exists(me.field_errors_label_map_key))
			{
				label = me.field_errors_label_map[field][
						me.field_errors_label_map_key];
			}

			// Append label
			for (var i in field_errors)
			{
				var error_message = [label, field_errors[i]].join(" ").trim();
				out_field_errors.push(error_message);
			}
		}
		else
		{
			out_field_errors = field_errors;
		}
		return out_field_errors;
	};

	me.merge_messages = function merge_messages(merge_display_messages)
	{
		var field_errors = merge_display_messages.field_errors();
		var field_warnings = merge_display_messages.field_warnings();
		var error_links = merge_display_messages.error_links();
		var standard_infos = merge_display_messages.standard_info();
		var standard_warnings = merge_display_messages.standard_warnings();
		var standard_errors = merge_display_messages.standard_errors();
		var alert_warnings = merge_display_messages.alert_warnings();
		var validation_warnings = merge_display_messages.validation_warnings();
		var standard_success = merge_display_messages.standard_success();

		for(var i = 0; i < standard_success.length; i++)
		{
			me.add_standard_success(standard_success[i]);
		}

		for(i = 0; i < standard_infos.length; i++)
		{
			me.add_standard_info(standard_infos[i]);
		}

		for(i = 0; i < standard_warnings.length; i++)
		{
			me.add_standard_warning(standard_warnings[i]);
		}

		for(i = 0; i < standard_errors.length; i++)
		{
			me.add_standard_error(standard_errors[i]);
		}

		for(i = 0; i < alert_warnings.length; i++)
		{
			me.add_alert_warning(alert_warnings[i]);
		}

		for(i = 0; i < validation_warnings.length; i++)
		{
			me.add_validation_warning(validation_warnings[i]);
		}

		for(i = 0; i < error_links.length; i++)
		{
			me.add_error_link(error_links[i]);
		}

		for(var key in field_errors)
		{
			if(field_errors.hasOwnProperty(key))
			{
				for(i = 0; i < field_errors[key].length; i++)
				{
					me.add_field_error(key, field_errors[key][i]);
				}
			}
		}
		for(var key in field_warnings)
		{
			if(field_warnings.hasOwnProperty(key))
			{
				for(i = 0; i < field_warnings[key].length; i++)
				{
					me.add_field_warning(key, field_warnings[key][i]);
				}
			}
		}
	};

	me.init();
};

