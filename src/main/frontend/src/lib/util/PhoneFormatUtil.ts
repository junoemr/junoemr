
export default class PhoneFormatUtils
{
	// ==========================================================================
	// Public Class Methods
	// ==========================================================================

	/**
	 * formats a phone number as (xxx) xxx-xxxx
	 * @param phoneNumber - the phone number to format
	 * @param stripInvalid - if true invalid characters will be removed
	 * @param limitLength - if true any characters after the end of the phone number will be removed
	 * @return the fromated phone number
	 */
	public static formatPhoneNumber(phoneNumber: string, stripInvalid: boolean = true, limitLength: boolean = true): string
	{
		if (phoneNumber)
		{
			phoneNumber = this.clearFormattingCharacters(phoneNumber);
			phoneNumber = this.stripLeadingOne(phoneNumber);
			if (stripInvalid)
			{
				phoneNumber = this.removeInvalidCharacters(phoneNumber);
			}
			if (limitLength)
			{
				phoneNumber = this.limitLength(phoneNumber);
			}
			phoneNumber = this.insertDigitSeparators(phoneNumber);
			phoneNumber = this.bracketFirstThreeDigits(phoneNumber);
		}
		return phoneNumber;
	}

	/**
	 * check if a phone number is of the correct format (xxx) xxx-xxxx
	 * @param phoneNumber - the phone number to check
	 * @return true / false indicating if the format is matched or not.
	 */
	public static isCorrectFormat(phoneNumber: string): boolean
	{
		return Boolean(phoneNumber.match(/\(\d{3}\)\s\d{3}-\d{4}/));
	}

	// ==========================================================================
	// Protected Class Methods
	// ==========================================================================


	// insert separators to get the phone number to format (xxx) xxx-xxxx
	protected static insertDigitSeparators(phoneNumber: string): string
	{
		if (phoneNumber.length >= 3)
		{
			phoneNumber = phoneNumber.replace(/^\(?(\d{3})/, "$1 ");
		}
		if (phoneNumber.length >= 7)
		{
			phoneNumber = phoneNumber.replace(/(\s\d{3})/, "$1-");
		}

		return phoneNumber;
	}

	// bracket the first 3 digits of the phone number like (xxx) xxx-xxxx
	protected static bracketFirstThreeDigits(phoneNumber: string): string
	{
		if (phoneNumber.trim().length >= 3)
		{
			// put the first 3 digits in brackets
			return phoneNumber.replace(/^(\d{3})/, "($1)");
		}
		return phoneNumber;
	}

	// remove all formatting characters from the phone number
	protected static clearFormattingCharacters(phoneNumber: string): string
	{
		// remove all "(", " ", "-" or ")" form phone number
		return phoneNumber.replace(/[()\s-]/g, "");
	}

	// remove characters not allowed in a phone number
	protected static removeInvalidCharacters(phoneNumber: string): string
	{
		return phoneNumber.replace(/[^\d]/g, "");
	}

	// limit the length of the phone number to 10 digits (strip every thing after).
	// Assumes a "clean" phone number with no formatting
	protected static limitLength(phoneNumber: string): string
	{
		return phoneNumber.slice(0, 10);
	}

	// strip leading one if applicable
	protected static stripLeadingOne(phoneNumber: string): string
	{
		if (phoneNumber.length > 10 && phoneNumber[0] === "1")
		{
			return phoneNumber.slice(1);
		}
		return phoneNumber;
	}
}
