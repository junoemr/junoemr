// ported from MHA
export default class HinValidator
{
	static readonly BASE_10 = 10;

	// ======================================================
	// Public Methods
	// ======================================================

	/**
	 * Validate health numbers via checksum only.  Does not account for if the number is actually issued or valid
	 * for a specific patient.
	 * @param provinceCode Two letter province code
	 * @param cardNumber Personal health number
	 * @param versionCode Optional version code
	 */
	public static healthCardNumber(provinceCode: string, cardNumber: string): boolean
	{
		if (!cardNumber)
		{
			return false;
		}

		switch (provinceCode)
		{
			case "BC":
			{
				return HinValidator.validateHealthCardBC(cardNumber);
			}
			case "ON":
			{
				return HinValidator.validateHealthCardON(cardNumber);
			}
			default:
			{
				return true;
			}
		}
	}

	// ======================================================
	// Private Methods
	// ======================================================

	/**
	 * Validate BC health card number
	 * @param cardNumber
	 */
	public static validateHealthCardBC(cardNumber)
	{
		if (cardNumber.length >= 10)
		{
			return this.validateHealthCardBCMod11(cardNumber);
		}
		else if (cardNumber.length === 8)
		{
			return this.numberAlphaCheck(cardNumber);
		}
		else
		{
			return this.validateHealthCardBCMod10(cardNumber);
		}
	}

	/**
	 * Validate BC PHN numbers
	 * https://www2.gov.bc.ca/assets/gov/health/practitioner-pro/software-development-guidelines/app_d.pdf
	 * @param phn BC health number
	 */
	private static validateHealthCardBCMod11(phn: string): boolean
	{
		// Spec states that health numbers are 13 digits, from  which leading zeroes that should be ignored.
		// Health cards have 10 digits on them.  If the leading zeroes are stripped from the 13 digit number,
		// It should match the health card number.  We'll strip any leading zeroes just to be safe.

		phn = phn.replace(/^0+/, "");

		if (!HinValidator.numberLengthCheck(phn, 10)
			|| !HinValidator.numberAlphaCheck(phn))
		{
			return false;
		}

		// Spec states the the first non-zero number must be 9, so might as well check for it.
		if (parseInt(phn.substring(0, 1), 10) !== 9)
		{
			return false;
		}

		const checkSumDigit = parseInt(phn.substring(9, 10), 10);
		const digitWeights = [2, 4, 8, 5, 10, 9, 7, 3];
		let sum = 0;

		// @ts-ignore
		Array.from(phn.substring(1, 9)).forEach((digit, index) =>
		{
			const element = parseInt(digit, 10);
			sum += (element * digitWeights[index]) % 11;
		});

		return (11 - (sum % 11)) === checkSumDigit;
	}

	//

	/**
	 * Deprecated Medical Services Plan Correctional Services Number.  This has been replaced by PHN (Mod11 Scheme)
	 * https://www2.gov.bc.ca/assets/gov/health/practitioner-pro/medical-services-plan/teleplan-v4-4.pdf
	 * Section 1.14.3
	 * @param cardNumber
	 */
	private static validateHealthCardBCMod10(cardNumber: string): boolean
	{
		// This number is usually 9 digits.  If less than 9, it should be left padded with 0s
		if (cardNumber.length < 9)
		{
			// @ts-ignore
			cardNumber = cardNumber.padStart(9, "0");
		}

		if (!HinValidator.numberLengthCheck(cardNumber, 9)
			|| !HinValidator.numberAlphaCheck(cardNumber))
		{
			return false;
		}

		const checkSumDigit = this.digitAt(cardNumber, 8);

		let sumA = 0;
		for (let i = 6; i >= 0; i -= 2)
		{
			sumA += this.digitAt(cardNumber, i);
		}

		let sumB = 0;
		for (let j = 7; j > 0; j -= 2)
		{
			sumB += this.doubleAndAddDigitAt(cardNumber, j);
		}

		const sumC = (sumA + sumB).toString();
		return  (10 - parseInt(sumC.slice(-1), 10)) === checkSumDigit;
	}


	/**
	 * Validate ON health card numbers
	 * http://health.gov.on.ca/english/providers/pub/ohip/tech_specific/pdf/5_13.pdf
	 * @param cardNumber ON health number
	 */
	private static validateHealthCardON(cardNumber: string): boolean
	{
		if (!HinValidator.numberLengthCheck(cardNumber, 10)
			|| !HinValidator.numberAlphaCheck(cardNumber))
		{
			return false;
		}

		let sum = 0;
		const checkSumDigit = parseInt(cardNumber.charAt(9), HinValidator.BASE_10);
		// @ts-ignore
		Array.from(cardNumber.substring(0, 9)).forEach((digitStr, index) =>
		{
			let digit = parseInt(digitStr, HinValidator.BASE_10);

			if ((index % 2) === 0)
			{
				const doubled = digit * 2;
				digit = (Math.floor(doubled / 10)) + (doubled % 10);
			}
			sum += digit;
		});
		return (checkSumDigit === ((10 - (sum % 10)) % 10));
	}

	/**
	 * Return false if the string representation of a number is not the specified length
	 * @param number
	 * @param length
	 */
	private static numberLengthCheck(number: string, length: number): boolean
	{
		return number.length === length;
	}

	/**
	 * Return false if the string representation of a number contains an alphabetic character;
	 * @param number
	 */
	private static numberAlphaCheck(number: string): boolean
	{
		return !isNaN(parseInt(number, 10));
	}

	/**
	 * Return the digit at the specified index
	 * @param number
	 * @param index
	 */
	private static digitAt(number: string, index: number): number
	{
		return parseInt(number[index], 10);
	}

	/**
	 * Double the digit at index, then add the digits of the result together.
	 * ie:  7 ==> (7 * 2) ==> 14 ==> 1 + 4 ==> 5
	 * @param number
	 * @param index
	 */
	private static doubleAndAddDigitAt(number: string, index: number)
	{
		const doubled = parseInt(number[index], 10) * 2;
		return Math.floor(doubled / 10) + doubled % 10;
	}
}