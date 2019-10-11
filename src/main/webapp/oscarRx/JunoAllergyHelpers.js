"use strict";

/*
 * Dependencies:
 * - moment.js
 * - jquery
 */

var Juno = Juno || {};

if (!Juno.AllergyHelpers)
{
	Juno.AllergyHelpers = {};
}

// *** Library constants so we don't have to redefine this everywhere ***
// Supported date formats. Structured specifically for momentjs
Juno.AllergyHelpers.DATE_FORMAT_YEAR_ONLY = "YYYY";
Juno.AllergyHelpers.DATE_FORMAT_YEAR_MONTH = "YYYY-MM";
Juno.AllergyHelpers.DATE_FORMAT_YEAR_MONTH_DAY = "YYYY-MM-DD";
// These numbers correspond to age in days
Juno.AllergyHelpers.LIFESTAGE_NEWBORN_LOWER_BOUND = 0;
Juno.AllergyHelpers.LIFESTAGE_NEWBORN_UPPER_BOUND = 28;
Juno.AllergyHelpers.LIFESTAGE_INFANT_LOWER_BOUND = 29;
// These constants correspond to age in years
Juno.AllergyHelpers.LIFESTAGE_INFANT_UPPER_BOUND = 2;
Juno.AllergyHelpers.LIFESTAGE_CHILD_LOWER_BOUND = 2;
Juno.AllergyHelpers.LIFESTAGE_CHILD_UPPER_BOUND = 15;
Juno.AllergyHelpers.LIFESTAGE_ADOLESCENT_LOWER_BOUND = 16;
Juno.AllergyHelpers.LIFESTAGE_ADOLESCENT_UPPER_BOUND = 17;
Juno.AllergyHelpers.LIFESTAGE_ADULT_LOWER_BOUND = 18;

/**
 * Given a string that should be a start date, confirm that its current format is valid under Oscar's old rules.
 * @param startDate a string that may be in format YYYY, YYYY-MM, or YYYY-MM-DD.
 * @return string date format, or null if we can't recognize the date format
 */
Juno.AllergyHelpers.isValidStartDate = function isValidStartDate(startDate)
{
	// empty start date is valid and fine
	if (startDate.length === 0)
	{
		return "";
	}

	// YYYY
	if (startDate.length === 4)
	{
		return Juno.AllergyHelpers.DATE_FORMAT_YEAR_ONLY;
	}

	// YYYY-MM
	if (startDate.length > 4 && startDate.length <= 7 && startDate[4] === "-")
	{
		return Juno.AllergyHelpers.DATE_FORMAT_YEAR_MONTH;
	}

	// Anything outside of the partial date formats, we can simply shove in to moment to see if it
	// constructs a valid date object.
	if (moment(startDate).isValid())
	{
		return Juno.AllergyHelpers.DATE_FORMAT_YEAR_MONTH_DAY;
	}

	return null;
};

/**
 * Given a start date and the format it's in, zero-pad the month and date.
 * @param startDate the date to pad as a string
 * @param dateFormat the format of the date string
 * @return string the original date string if in YYYY format or in an unknown format, padded date string otherwise
 */
Juno.AllergyHelpers.padStartDate = function padStartDate(startDate, dateFormat)
{
	// to make this play nicely with other date validation, we'll treat it as end-of-year
	if (dateFormat === Juno.AllergyHelpers.DATE_FORMAT_YEAR_ONLY)
	{
		startDate += "-12-31";
		return startDate;
	}

	if (dateFormat === Juno.AllergyHelpers.DATE_FORMAT_YEAR_MONTH
		|| dateFormat === Juno.AllergyHelpers.DATE_FORMAT_YEAR_MONTH_DAY)
	{
		var splitDate = startDate.split("-");
		if (splitDate.length >= 2 && splitDate[1].length === 1)
		{
			splitDate[1] = "0" + splitDate[1];
		}

		if (splitDate.length === 3 && splitDate[2].length === 1)
		{
			splitDate[2] = "0" + splitDate[2];
		}

		return splitDate.join("-");
	}

	return startDate;
};

/**
 * Given a demographic and identifying information for an allergy being added, check if there are any
 * active entries that are similar to the allergy being added.
 * @param demographicNo demographic to add allergy for
 * @param typeCode type of drug being added (0 if a custom drug)
 * @param drugName user-friendly text name for the drug
 * @param currEntryId primary key of the allergy being added/modified, if available
 * @return {boolean} true if a duplicate allergy exists, false otherwise
 */
Juno.AllergyHelpers.isDuplicateAllergy = function isDuplicateAllergy(demographicNo,
																	 typeCode,
																	 drugName,
																	 currEntryId)
{
	var isDuplicate = false;
	$.ajax({
		url: "../ws/rs/allergies/active?demographicNo=" + demographicNo,
		method: "GET",
		async: false,
		success: function(data)
		{
			for (var i = 0; i < data.allergies.length; i++)
			{
				var allergyEntry = data.allergies[i];
				// Even if we find a duplicate allergy, user should be allowed to modify an existing active entry
				if (String(allergyEntry['id']) === currEntryId)
				{
					isDuplicate = false;
					break;
				}

				if (String(allergyEntry['typeCode']) === typeCode &&
					allergyEntry['description'] === drugName)
				{
					isDuplicate = true;
				}
			}
		}
	});
	return isDuplicate;
};

/**
 * Given a date of onset as well as the patient's input lifestage and their date of birth, determine
 * whether the date of onset actually makes sense for the demographic.
 * @param lifestage code corresponding to lifestage of a demographic
 * @param birthday D.O.B. of demographic
 * @param compareDate date to check against
 * @return boolean: true if lifestage + birthday combinations border either side of the compareDate, false otherwise
 */
Juno.AllergyHelpers.checkDateAgainstLifestage = function checkDateAgainstLifestage(lifestage, birthday, compareDate)
{
	// Not having lifestage set is OK
	if (lifestage === '')
	{
		return true;
	}

	var floorDate = Juno.AllergyHelpers.getLowerBoundLifestageDate(lifestage, birthday);
	var ceilingDate = Juno.AllergyHelpers.getUpperBoundLifestageDate(lifestage, birthday);

	compareDate = moment(compareDate);

	return floorDate <= compareDate && ceilingDate >= compareDate;
};

/**
 * Given lifestage of patient and a date, adjust the date based off the lower bound of the lifestage.
 * @param lifestage code corresponding to life stage of individual
 * @param date String in 'yyyy-MM-dd' format
 * @return {string} Date string in 'yyyy-MM-dd' based off lifestage lower bound
 */
Juno.AllergyHelpers.getLowerBoundLifestageDate = function getLowerBoundLifestageDate(lifestage, date)
{
	date = moment(date);
	switch(lifestage)
	{
		case 'I':
			date = date.add(Juno.AllergyHelpers.LIFESTAGE_INFANT_LOWER_BOUND, 'days');
			break;
		case 'C':
			date = date.add(Juno.AllergyHelpers.LIFESTAGE_CHILD_LOWER_BOUND, 'years');
			break;
		case 'T':
			date = date.add(Juno.AllergyHelpers.LIFESTAGE_ADOLESCENT_LOWER_BOUND, 'years');
			break;
		case 'A':
			date = date.add(Juno.AllergyHelpers.LIFESTAGE_ADULT_LOWER_BOUND, 'years');
			break;
		case 'N':
		default:
			break;
	}
	return date;
};

/**
 * Given lifestage of patient and a date, adjust the date based off the upper bound of the lifestage.
 * @param lifestage code corresponding to life stage of individual
 * @param date String in 'yyyy-MM-dd' format
 * @return {string} Date string in 'yyyy-MM-dd' adjusted based off lifestage upper bound
 */
Juno.AllergyHelpers.getUpperBoundLifestageDate = function getUpperBoundLifestageDate(lifestage, date)
{
	date = moment(date);
	switch(lifestage)
	{
		case 'N':
			date = date.add(Juno.AllergyHelpers.LIFESTAGE_NEWBORN_UPPER_BOUND, 'days');
			break;
		case 'I':
			date = date.add(Juno.AllergyHelpers.LIFESTAGE_INFANT_UPPER_BOUND, 'years');
			break;
		case 'C':
			date = date.add(Juno.AllergyHelpers.LIFESTAGE_CHILD_UPPER_BOUND, 'years');
			break;
		case 'T':
			date = date.add(Juno.AllergyHelpers.LIFESTAGE_ADOLESCENT_UPPER_BOUND, 'years');
			break;
		case 'A':
			// We don't have an explicit upper bound for this, so just return today's date
			date = moment();
			break;
		default:
			break;
	}
	return date;
};

/**
 * primarily here so we don't have to hit up the Java side every time to get this
 * @param lifestage letter code corresponding to a life stage
 * @return {string} full name of the lifestage
 */
Juno.AllergyHelpers.getLifestageName = function getLifestageName(lifestage)
{
	switch(lifestage)
	{
		case 'N':
			return 'Newborn';
		case 'I':
			return 'Infant';
		case 'C':
			return 'Child';
		case 'T':
			return 'Adolescent';
		case 'A':
			return 'Adult';
		default:
			return '';
	}
};

/**
 * Given a lifestage code, return the lower bound for expected age.
 * @param lifestage
 */
Juno.AllergyHelpers.getLowerBoundLifestageAge = function getLowerBoundLifestageAge(lifestage)
{
	switch(lifestage)
	{
		case 'N':
			return Juno.AllergyHelpers.LIFESTAGE_NEWBORN_LOWER_BOUND;
		case 'I':
			return Juno.AllergyHelpers.LIFESTAGE_INFANT_LOWER_BOUND;
		case 'C':
			return Juno.AllergyHelpers.LIFESTAGE_CHILD_LOWER_BOUND;
		case 'T':
			return Juno.AllergyHelpers.LIFESTAGE_ADOLESCENT_LOWER_BOUND;
		case 'A':
			return Juno.AllergyHelpers.LIFESTAGE_ADULT_LOWER_BOUND;
		default:
			return 0;
	}
};

/**
 * Given a lifestage code, return the upper bound for expected age.
 * @param lifestage
 */
Juno.AllergyHelpers.getUpperBoundLifestageAge = function getUpperBoundLifestageAge(lifestage)
{
	switch(lifestage)
	{
		case 'N':
			return Juno.AllergyHelpers.LIFESTAGE_NEWBORN_UPPER_BOUND;
		case 'I':
			return Juno.AllergyHelpers.LIFESTAGE_INFANT_UPPER_BOUND;
		case 'C':
			return Juno.AllergyHelpers.LIFESTAGE_CHILD_UPPER_BOUND;
		case 'T':
			return Juno.AllergyHelpers.LIFESTAGE_ADOLESCENT_UPPER_BOUND;
		case 'A':
		default:
			break;
	}
};