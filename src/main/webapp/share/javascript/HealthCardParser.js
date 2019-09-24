/*

    Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for
    CloudPractice Inc.
    Victoria, British Columbia
    Canada

*/
'use strict';

if (!window.Oscar)
{
	window.Oscar = {}
}
if (!Oscar.HealthCardParser)
{
	Oscar.HealthCardParser = {}
}

var BC_STANDALONE = "%B610043";
var BC_PREFIX = "%BC";
var BC_COMBINED = "?;636028";
var ON_STANDALONE = "%B610054";

Oscar.HealthCardParser.isWhitelisted = function isWhitelisted(cardNo)
{
	cardNo = cardNo.toUpperCase();
	return (cardNo.startsWith(BC_STANDALONE) ||
			cardNo.startsWith(ON_STANDALONE) ||
			(cardNo.startsWith(BC_PREFIX) && cardNo.substring(cardNo.indexOf("?")).startsWith(BC_COMBINED)));
};

/**
 * take in separated year, month, day values and ensure that their combination
 * produces a valid date
 * @param year
 * @param month
 * @param day
 */
Oscar.HealthCardParser.validateDate = function validateDate(year, month, day)
{
	var dateString = year + "-" + month + "-" + day;
	return !(isNaN(Date.parse(dateString)));
};

Oscar.HealthCardParser.getFieldValue = function getFieldValue(track, trackIndex, length)
{
	var fieldValue = "";
	try
	{
		// fields where the value is shorter than the length use ^ as a terminator
		var terminationIndex = track.indexOf("^", trackIndex);
		//if no terminator OR terminator outside of field
		if (terminationIndex < 0 || terminationIndex > length)
		{
			// get the string using the index and length
			fieldValue = track.substring(trackIndex, trackIndex + length);
		}
		else
		{
			//get the string using the index and terminator
			fieldValue = track.substring(trackIndex, terminationIndex);
		}
	}
	catch(e)
	{
		console.err("Error reading data field", e);
	}
	return fieldValue.trim();
};

Oscar.HealthCardParser.parseBCCombined = function parseBCCombined(cardData,cardHash)
{
	var dataHash = cardHash.data;
	var metaHash = cardHash.meta;

	var track1Index=0;
	var track2Index=cardData.indexOf("?") + 1;
	var track3Index=cardData.indexOf("?", track2Index) + 1;

	var track1 = cardData.substring(track1Index, track2Index);
	var track2 = cardData.substring(track2Index, track3Index);
	var track3 = cardData.substring(track3Index);

	//TRACK 1
	// length constants taken from specification
	var index = 1; // skip % Constant
	dataHash.province = Oscar.HealthCardParser.getFieldValue(track1,index,2);
	index += dataHash.province.length;

	dataHash.city = Oscar.HealthCardParser.getFieldValue(track1,index,13);
	index += dataHash.city.length+1;
	var names = Oscar.HealthCardParser.getFieldValue(track1,index,35);
	index += names.length+1;
	var fullAddress = Oscar.HealthCardParser.getFieldValue(track1,index,29);

	dataHash.lastName = names.substring(0, names.indexOf("$")-1).toUpperCase();
	dataHash.firstName = names.substring(names.indexOf("$")+1).toUpperCase();
	dataHash.address = (fullAddress.includes("$"))? fullAddress.substring(0, fullAddress.indexOf("$")) : fullAddress;

	//TRACK 2
	index = 1; // skip track 2 constant ;
	metaHash.isoiin = Oscar.HealthCardParser.getFieldValue(track2,index,6);
	index += metaHash.isoiin.length;
	var DL = track2.substring(index, track2.indexOf("="));
	index += DL.length;
	if(DL.length === 7) {
		dataHash.driverLicenseNo = DL;
	}
	else
	{
		dataHash.clientNo = DL;
	}

	index++; // skip constant '=' in field 4
	var expire = Oscar.HealthCardParser.getFieldValue(track2,index,4); //YYMM
	index += expire.length;
	if("0000" !== expire) {
		dataHash.endYear = "20" + expire.substring(0, 2);
		dataHash.endMonth = expire.substring(2, 4);
		dataHash.endDay = "01";
	}

	var dobCCYYMMDD = Oscar.HealthCardParser.getFieldValue(track2,index,8);

	dataHash.dobYear = dobCCYYMMDD.substring(0, 4);
	dataHash.dobMonth = dobCCYYMMDD.substring(4, 6);
	dataHash.dobDay = dobCCYYMMDD.substring(6, 8);

	//TRACK 3

	index=1; // skip 1 character constant
	metaHash.versionNo = Oscar.HealthCardParser.getFieldValue(track3,index,1);
	index += 1;
	metaHash.secVersionNo = Oscar.HealthCardParser.getFieldValue(track3,index,1);
	index += 1;

	dataHash.postal = Oscar.HealthCardParser.getFieldValue(track3,index,11);
	index += 11;
	dataHash.class = Oscar.HealthCardParser.getFieldValue(track3,index,2);
	index += 2;
	dataHash.restrictions = Oscar.HealthCardParser.getFieldValue(track3,index,10);
	index += 10;
	dataHash.endorsements = Oscar.HealthCardParser.getFieldValue(track3,index,4);
	index += 4;
	dataHash.sex = Oscar.HealthCardParser.getFieldValue(track3,index,1);
	index += 1;
	dataHash.height = Oscar.HealthCardParser.getFieldValue(track3,index,3);
	index += 3;
	dataHash.weight = Oscar.HealthCardParser.getFieldValue(track3,index,3);
	index += 3;
	dataHash.hairColour = Oscar.HealthCardParser.getFieldValue(track3,index,3);
	index += 3;
	dataHash.eyeColour = Oscar.HealthCardParser.getFieldValue(track3,index,3);
	index += 3;
	dataHash.hin = Oscar.HealthCardParser.getFieldValue(track3,index,10);
	index += 10;

	metaHash.fourteenA = Oscar.HealthCardParser.getFieldValue(track3,index,16);
	index += 16;
	metaHash.ecc = Oscar.HealthCardParser.getFieldValue(track3,index,6);
	index += 6;
	metaHash.securityFunction = Oscar.HealthCardParser.getFieldValue(track3,index,5);

	cardHash.meta = metaHash;
	cardHash.data = dataHash;

	return cardHash;
};

Oscar.HealthCardParser.parseBCStandalone = function parseBCStandalone(cardData,cardHash)
{
	var dataHash = cardHash.data;
	var metaHash = cardHash.meta;

	// BC standalone photo card, non-photo card, or care card
	// https://www2.gov.bc.ca/assets/gov/health/practitioner-pro/medical-services-plan/teleplan-v4-4.pdf
	// begins p. 106
	dataHash.hin = cardData.substring(8, cardData.indexOf("0^"));
	dataHash.lastName = cardData.substring(cardData.indexOf("^")+1, cardData.indexOf("/")).toUpperCase();

	var subcard = cardData.substring(cardData.indexOf("/")+1);
	dataHash.firstName = subcard.substring(0,subcard.indexOf("^")).toUpperCase();

	subcard = subcard.substring(subcard.indexOf("^")+1);
	var endYYMM = subcard.substring(0, 4);
	if("0000" !== endYYMM) {
		dataHash.endYear = "20" + endYYMM.substring(0, 2);
		dataHash.endMonth = endYYMM.substring(2, 4);
		dataHash.endDay = "01";
	}

	var issuerYYMM = subcard.substring(4, 8);
	if("0000" !== issuerYYMM) {
		dataHash.effYear = "20" + issuerYYMM.substring(0, 2);
		dataHash.effMonth = issuerYYMM.substring(2, 4);
		dataHash.effDay = "01";
	}

	var dobCCYYMMDD = subcard.substring(8, 16);
	dataHash.dobYear = dobCCYYMMDD.substring(0, 4);
	dataHash.dobMonth = dobCCYYMMDD.substring(4, 6);
	dataHash.dobDay = dobCCYYMMDD.substring(6, 8);

	dataHash.sex = 'M'; // this is not included in spec, default to Male
	dataHash.province = 'BC';

	cardHash.meta = metaHash;
	cardHash.data = dataHash;

	return cardHash;
};

Oscar.HealthCardParser.parseOntario = function parseOntario(cardData,cardHash)
{
	var dataHash = cardHash.data;
	var metaHash = cardHash.meta;

	dataHash.hin = cardData.substring(8, cardData.indexOf("^"));
	dataHash.lastName = cardData.substring(cardData.indexOf("^") + 1, cardData.indexOf("/")).toUpperCase();

	var subcard = cardData.substring(cardData.indexOf("/") + 1);
	dataHash.firstName = subcard.substring(0, subcard.indexOf("^")).toUpperCase();
	dataHash.dobYear = subcard.substring(subcard.indexOf("^") + 9, subcard.indexOf("^") + 13);
	dataHash.dobMonth = subcard.substring(subcard.indexOf("^") + 13, subcard.indexOf("^") + 15);
	dataHash.dobDay = subcard.substring(subcard.indexOf("^") + 15, subcard.indexOf("^") + 17);
	dataHash.versionCode = subcard.substring(subcard.indexOf("^") + 17, subcard.indexOf("^") + 19);
	dataHash.versionCode = dataHash.versionCode.toUpperCase();

	var monthInt = parseInt(subcard.substring(subcard.indexOf("^") + 1, subcard.indexOf("^") + 3));
	dataHash.endYear = (monthInt > 30 ? "19" : "20") + subcard.substring(subcard.indexOf("^") + 1, subcard.indexOf("^") + 3);
	dataHash.endMonth = subcard.substring(subcard.indexOf("^") + 3, subcard.indexOf("^") + 5);
	dataHash.endDay = dataHash.dobDay;

	monthInt = parseInt(subcard.substring(subcard.indexOf("^") + 24, subcard.indexOf("^") + 26));
	dataHash.effYear = (monthInt > 30 ? "19" : "20") + subcard.substring(subcard.indexOf("^") + 24, subcard.indexOf("^") + 26);
	dataHash.effMonth = subcard.substring(subcard.indexOf("^") + 26, subcard.indexOf("^") + 28);
	dataHash.effDay = subcard.substring(subcard.indexOf("^") + 28, subcard.indexOf("^") + 30);

	var sex = subcard.substring(subcard.indexOf("^") + 8, subcard.indexOf("^") + 9);
	if (sex === "2")
	{
		dataHash.sex = "F";
	}
	else
	{
		dataHash.sex = "M";
	}
	dataHash.province = 'ON';

	cardHash.meta = metaHash;
	cardHash.data = dataHash;

	return cardHash;
};


Oscar.HealthCardParser.parse = function parse(cardData)
{
	var cardHashOut = {
		meta: {},
		data: {}
	};
	try
	{
		cardData = cardData.toUpperCase();
		console.info(cardData);

		if (cardData.startsWith(BC_STANDALONE))
		{
			cardHashOut = Oscar.HealthCardParser.parseBCStandalone(cardData, cardHashOut);
		}
		else if (cardData.startsWith(BC_PREFIX) && cardData.substring(cardData.indexOf("?")).startsWith(BC_COMBINED))
		{
			cardHashOut = Oscar.HealthCardParser.parseBCCombined(cardData, cardHashOut);
		}
		else
		{
			// Card may be unsupported, but as a temporary measure we're letting it through
			cardHashOut = Oscar.HealthCardParser.parseOntario(cardData, cardHashOut);
		}
	}
	catch (e)
	{
		console.error("Error parsing card data", e);
	}
	return cardHashOut;
};


