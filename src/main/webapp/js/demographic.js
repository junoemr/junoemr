

Oscar.demographic.validatePhoneNumber = function validatePhoneNumber(event)
{
	event.stopPropagation();
	var userInput = jQuery("#"+event.target.id).val();
	var lengthBeforeProcess = userInput.length;

	if (lengthBeforeProcess > 0)
	{
		var array = Oscar.demographic.removeUnicodeControlCharacters(userInput);
		var validatedInput = array.join('');
		var lengthAfterProcess = validatedInput.length;

		if (event.target.id === "phone" || event.target.id === "phone2" )
		{
			if (validatedInput.length === 10)
			{
				validatedInput = validatedInput.substring(0,3) + "-" + validatedInput.substring(3,6) + "-" + validatedInput.substring(6);
			}
			else if (validatedInput.length === 11)
			{
				validatedInput = validatedInput.substring(0,3) + "-" + validatedInput.substring(4,7) + "-" + validatedInput.substring(7);
			}
		}
		jQuery("#"+event.target.id).val(validatedInput);
		if (lengthBeforeProcess !== lengthAfterProcess)
		{
			alert("Invalid character for phone numbers has been removed!");
		}
	}
}

Oscar.demographic.removeUnicodeControlCharacters = function removeUnicodeControlCharacters(stringInput)
{
	var array = stringInput.split('');
	return array.filter(
		x =>
		(32 <= x.charCodeAt(0) && x.charCodeAt(0) <=126) ||
		x.charCodeAt(0) > 160
	);
}
