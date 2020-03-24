

function validatePhoneNumber(event)
{
	event.stopPropagation();
	let userInput = jQuery("#"+event.target.id).val();
	let lengthBeforeProcess = userInput.length;

	if (lengthBeforeProcess > 0)
	{
		let array = userInput.split('');
		array = removeUnicodeControlCharacters(array);
		let validatedInput = array.join('');
		let lengthAfterProcess = validatedInput.length;

		if (event.target.id === "phone" || event.target.id === "phone2" )
		{
			if (validatedInput.length == 10)
			{
				validatedInput = validatedInput.substring(0,3) + "-" + validatedInput.substring(3,6) + "-" + validatedInput.substring(6);
			}
			else if (validatedInput.length == 11)
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

function removeUnicodeControlCharacters(array)
{
	return array.filter(
		x =>
		(34 <= x.charCodeAt(0) && x.charCodeAt(0) <=124) ||
		x.charCodeAt(0) > 159
	);
}
