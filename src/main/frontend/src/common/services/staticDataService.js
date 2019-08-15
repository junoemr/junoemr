'use strict';

/*

 Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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

 This software was written for the
 Department of Family Medicine
 McMaster University
 Hamilton
 Ontario, Canada

 */
angular.module("Common.Services").service("staticDataService", [
	function()
	{
		var service = {};

		service.getGenders = function getGenders()
		{
			var genders = [];
			genders.push(
			{
				"value": "M",
				"label": "Male"
			});
			genders.push(
			{
				"value": "F",
				"label": "Female"
			});
			genders.push(
			{
				"value": "T",
				"label": "Transgender"
			});
			genders.push(
			{
				"value": "O",
				"label": "Other"
			});
			genders.push(
			{
				"value": "U",
				"label": "Undefined"
			});
			return genders;
		};

		service.getTitles = function getTitles()
		{
			var titles = [];
			titles.push(
			{
				"value": "MISS",
				"label": "MISS"
			});
			titles.push(
			{
				"value": "MRS",
				"label": "MRS"
			});
			titles.push(
			{
				"value": "MS",
				"label": "MS"
			});
			titles.push(
			{
				"value": "MR",
				"label": "MR"
			});
			titles.push(
			{
				"value": "MSSR",
				"label": "MSSR"
			});
			titles.push(
			{
				"value": "DR",
				"label": "DR"
			});
			titles.push(
			{
				"value": "PROF",
				"label": "PROF"
			});
			titles.push(
			{
				"value": "REEVE",
				"label": "REEVE"
			});
			titles.push(
			{
				"value": "REV",
				"label": "REV"
			});
			titles.push(
			{
				"value": "RT_HON",
				"label": "RT_HON"
			});
			titles.push(
			{
				"value": "SEN",
				"label": "SEN"
			});
			titles.push(
			{
				"value": "SGT",
				"label": "SGT"
			});
			titles.push(
			{
				"value": "SR",
				"label": "SR"
			});
			return titles;
		};

		service.getProvinces = function getProvinces()
		{
			var provinces = [];
			provinces.push(
			{
				"value": "AB",
				"label": "AB-Alberta"
			});
			provinces.push(
			{
				"value": "BC",
				"label": "BC-British Columbia"
			});
			provinces.push(
			{
				"value": "MB",
				"label": "MB-Manitoba"
			});
			provinces.push(
			{
				"value": "NB",
				"label": "NB-New Brunswick"
			});
			provinces.push(
			{
				"value": "NL",
				"label": "NL-Newfoundland Labrador"
			});
			provinces.push(
			{
				"value": "NT",
				"label": "NT-Northwest Territory"
			});
			provinces.push(
			{
				"value": "NS",
				"label": "NS-Nova Scotia"
			});
			provinces.push(
			{
				"value": "NU",
				"label": "NU-Nunavut"
			});
			provinces.push(
			{
				"value": "ON",
				"label": "ON-Ontario"
			});
			provinces.push(
			{
				"value": "PE",
				"label": "PE-Prince Edward Island"
			});
			provinces.push(
			{
				"value": "QC",
				"label": "QC-Quebec"
			});
			provinces.push(
			{
				"value": "SK",
				"label": "SK-Saskatchewan"
			});
			provinces.push(
			{
				"value": "YT",
				"label": "YT-Yukon"
			});
			provinces.push(
			{
				"value": "US",
				"label": "US resident"
			});
			provinces.push(
			{
				"value": "US-AK",
				"label": "US-AK-Alaska"
			});
			provinces.push(
			{
				"value": "US-AL",
				"label": "US-AL-Alabama"
			});
			provinces.push(
			{
				"value": "US-AR",
				"label": "US-AR-Arkansas"
			});
			provinces.push(
			{
				"value": "US-AZ",
				"label": "US-AZ-Arizona"
			});
			provinces.push(
			{
				"value": "US-CA",
				"label": "US-CA-California"
			});
			provinces.push(
			{
				"value": "US-CO",
				"label": "US-CO-Colorado"
			});
			provinces.push(
			{
				"value": "US-CT",
				"label": "US-CT-Connecticut"
			});
			provinces.push(
			{
				"value": "US-CZ",
				"label": "US-CZ-Canal Zone"
			});
			provinces.push(
			{
				"value": "US-DC",
				"label": "US-DC-District Of Columbia"
			});
			provinces.push(
			{
				"value": "US-DE",
				"label": "US-DE-Delaware"
			});
			provinces.push(
			{
				"value": "US-FL",
				"label": "US-FL-Florida"
			});
			provinces.push(
			{
				"value": "US-GA",
				"label": "US-GA-Georgia"
			});
			provinces.push(
			{
				"value": "US-GU",
				"label": "US-GU-Guam"
			});
			provinces.push(
			{
				"value": "US-HI",
				"label": "US-HI-Hawaii"
			});
			provinces.push(
			{
				"value": "US-IA",
				"label": "US-IA-Iowa"
			});
			provinces.push(
			{
				"value": "US-ID",
				"label": "US-ID-Idaho"
			});
			provinces.push(
			{
				"value": "US-IL",
				"label": "US-IL-Illinois"
			});
			provinces.push(
			{
				"value": "US-IN",
				"label": "US-IN-Indiana"
			});
			provinces.push(
			{
				"value": "US-KS",
				"label": "US-KS-Kansas"
			});
			provinces.push(
			{
				"value": "US-KY",
				"label": "US-KY-Kentucky"
			});
			provinces.push(
			{
				"value": "US-LA",
				"label": "US-LA-Louisiana"
			});
			provinces.push(
			{
				"value": "US-MA",
				"label": "US-MA-Massachusetts"
			});
			provinces.push(
			{
				"value": "US-MD",
				"label": "US-MD-Maryland"
			});
			provinces.push(
			{
				"value": "US-ME",
				"label": "US-ME-Maine"
			});
			provinces.push(
			{
				"value": "US-MI",
				"label": "US-MI-Michigan"
			});
			provinces.push(
			{
				"value": "US-MN",
				"label": "US-MN-Minnesota"
			});
			provinces.push(
			{
				"value": "US-MO",
				"label": "US-MO-Missouri"
			});
			provinces.push(
			{
				"value": "US-MS",
				"label": "US-MS-Mississippi"
			});
			provinces.push(
			{
				"value": "US-MT",
				"label": "US-MT-Montana"
			});
			provinces.push(
			{
				"value": "US-NC",
				"label": "US-NC-North Carolina"
			});
			provinces.push(
			{
				"value": "US-ND",
				"label": "US-ND-North Dakota"
			});
			provinces.push(
			{
				"value": "US-NE",
				"label": "US-NE-Nebraska"
			});
			provinces.push(
			{
				"value": "US-NH",
				"label": "US-NH-New Hampshire"
			});
			provinces.push(
			{
				"value": "US-NJ",
				"label": "US-NJ-New Jersey"
			});
			provinces.push(
			{
				"value": "US-NM",
				"label": "US-NM-New Mexico"
			});
			provinces.push(
			{
				"value": "US-NU",
				"label": "US-NU-Nunavut"
			});
			provinces.push(
			{
				"value": "US-NV",
				"label": "US-NV-Nevada"
			});
			provinces.push(
			{
				"value": "US-NY",
				"label": "US-NY-New York"
			});
			provinces.push(
			{
				"value": "US-OH",
				"label": "US-OH-Ohio"
			});
			provinces.push(
			{
				"value": "US-OK",
				"label": "US-OK-Oklahoma"
			});
			provinces.push(
			{
				"value": "US-OR",
				"label": "US-OR-Oregon"
			});
			provinces.push(
			{
				"value": "US-PA",
				"label": "US-PA-Pennsylvania"
			});
			provinces.push(
			{
				"value": "US-PR",
				"label": "US-PR-Puerto Rico"
			});
			provinces.push(
			{
				"value": "US-RI",
				"label": "US-RI-Rhode Island"
			});
			provinces.push(
			{
				"value": "US-SC",
				"label": "US-SC-South Carolina"
			});
			provinces.push(
			{
				"value": "US-SD",
				"label": "US-SD-South Dakota"
			});
			provinces.push(
			{
				"value": "US-TN",
				"label": "US-TN-Tennessee"
			});
			provinces.push(
			{
				"value": "US-TX",
				"label": "US-TX-Texas"
			});
			provinces.push(
			{
				"value": "US-UT",
				"label": "US-UT-Utah"
			});
			provinces.push(
			{
				"value": "US-VA",
				"label": "US-VA-Virginia"
			});
			provinces.push(
			{
				"value": "US-VI",
				"label": "US-VI-Virgin Islands"
			});
			provinces.push(
			{
				"value": "US-VT",
				"label": "US-VT-Vermont"
			});
			provinces.push(
			{
				"value": "US-WA",
				"label": "US-WA-Washington"
			});
			provinces.push(
			{
				"value": "US-WI",
				"label": "US-WI-Wisconsin"
			});
			provinces.push(
			{
				"value": "US-WV",
				"label": "US-WV-West Virginia"
			});
			provinces.push(
			{
				"value": "US-WY",
				"label": "US-WY-Wyoming"
			});
			provinces.push(
			{
				"value": "OT",
				"label": "Other"
			});
			return provinces;
		};

		service.getCountries = function getCountries()
		{
			var countries = [];
			countries.push(
			{
				"value": "AF",
				"label": "AFGHANISTAN"
			});
			countries.push(
			{
				"value": "AX",
				"label": "ALAND ISLANDS"
			});
			countries.push(
			{
				"value": "AL",
				"label": "ALBANIA"
			});
			countries.push(
			{
				"value": "DZ",
				"label": "ALGERIA"
			});
			countries.push(
			{
				"value": "AS",
				"label": "AMERICAN SAMOA"
			});
			countries.push(
			{
				"value": "AD",
				"label": "ANDORRA"
			});
			countries.push(
			{
				"value": "AO",
				"label": "ANGOLA"
			});
			countries.push(
			{
				"value": "AI",
				"label": "ANGUILLA"
			});
			countries.push(
			{
				"value": "AQ",
				"label": "ANTARCTICA"
			});
			countries.push(
			{
				"value": "AG",
				"label": "ANTIGUA AND BARBUDA"
			});
			countries.push(
			{
				"value": "AR",
				"label": "ARGENTINA"
			});
			countries.push(
			{
				"value": "AM",
				"label": "ARMENIA"
			});
			countries.push(
			{
				"value": "AW",
				"label": "ARUBA"
			});
			countries.push(
			{
				"value": "AU",
				"label": "AUSTRALIA"
			});
			countries.push(
			{
				"value": "AT",
				"label": "AUSTRIA"
			});
			countries.push(
			{
				"value": "AZ",
				"label": "AZERBAIJAN"
			});
			countries.push(
			{
				"value": "BS",
				"label": "BAHAMAS"
			});
			countries.push(
			{
				"value": "BH",
				"label": "BAHRAIN"
			});
			countries.push(
			{
				"value": "BD",
				"label": "BANGLADESH"
			});
			countries.push(
			{
				"value": "BB",
				"label": "BARBADOS"
			});
			countries.push(
			{
				"value": "BY",
				"label": "BELARUS"
			});
			countries.push(
			{
				"value": "BE",
				"label": "BELGIUM"
			});
			countries.push(
			{
				"value": "BZ",
				"label": "BELIZE"
			});
			countries.push(
			{
				"value": "BJ",
				"label": "BENIN"
			});
			countries.push(
			{
				"value": "BM",
				"label": "BERMUDA"
			});
			countries.push(
			{
				"value": "BT",
				"label": "BHUTAN"
			});
			countries.push(
			{
				"value": "BO",
				"label": "BOLIVIA"
			});
			countries.push(
			{
				"value": "BA",
				"label": "BOSNIA AND HERZEGOVINA"
			});
			countries.push(
			{
				"value": "BW",
				"label": "BOTSWANA"
			});
			countries.push(
			{
				"value": "BV",
				"label": "BOUVET ISLAND"
			});
			countries.push(
			{
				"value": "BR",
				"label": "BRAZIL"
			});
			countries.push(
			{
				"value": "IO",
				"label": "BRITISH INDIAN OCEAN TERRITORY"
			});
			countries.push(
			{
				"value": "BN",
				"label": "BRUNEI DARUSSALAM"
			});
			countries.push(
			{
				"value": "BG",
				"label": "BULGARIA"
			});
			countries.push(
			{
				"value": "BF",
				"label": "BURKINA FASO"
			});
			countries.push(
			{
				"value": "BI",
				"label": "BURUNDI"
			});
			countries.push(
			{
				"value": "KH",
				"label": "CAMBODIA"
			});
			countries.push(
			{
				"value": "CM",
				"label": "CAMEROON"
			});
			countries.push(
			{
				"value": "CA",
				"label": "CANADA"
			});
			countries.push(
			{
				"value": "CV",
				"label": "CAPE VERDE"
			});
			countries.push(
			{
				"value": "KY",
				"label": "CAYMAN ISLANDS"
			});
			countries.push(
			{
				"value": "CF",
				"label": "CENTRAL AFRICAN REPUBLIC"
			});
			countries.push(
			{
				"value": "TD",
				"label": "CHAD"
			});
			countries.push(
			{
				"value": "CL",
				"label": "CHILE"
			});
			countries.push(
			{
				"value": "CN",
				"label": "CHINA"
			});
			countries.push(
			{
				"value": "CX",
				"label": "CHRISTMAS ISLAND"
			});
			countries.push(
			{
				"value": "CC",
				"label": "COCOS (KEELING) ISLANDS"
			});
			countries.push(
			{
				"value": "CO",
				"label": "COLOMBIA"
			});
			countries.push(
			{
				"value": "KM",
				"label": "COMOROS"
			});
			countries.push(
			{
				"value": "CG",
				"label": "CONGO"
			});
			countries.push(
			{
				"value": "CD",
				"label": "CONGO, THE DEMOCRATIC REPUBLIC OF THE"
			});
			countries.push(
			{
				"value": "CK",
				"label": "COOK ISLANDS"
			});
			countries.push(
			{
				"value": "CR",
				"label": "COSTA RICA"
			});
			countries.push(
			{
				"value": "CI",
				"label": "CÃ¯TE D IVOIRE"
			});
			countries.push(
			{
				"value": "HR",
				"label": "CROATIA"
			});
			countries.push(
			{
				"value": "CU",
				"label": "CUBA"
			});
			countries.push(
			{
				"value": "CY",
				"label": "CYPRUS"
			});
			countries.push(
			{
				"value": "CZ",
				"label": "CZECH REPUBLIC"
			});
			countries.push(
			{
				"value": "DK",
				"label": "DENMARK"
			});
			countries.push(
			{
				"value": "DJ",
				"label": "DJIBOUTI"
			});
			countries.push(
			{
				"value": "DM",
				"label": "DOMINICA"
			});
			countries.push(
			{
				"value": "DO",
				"label": "DOMINICAN REPUBLIC"
			});
			countries.push(
			{
				"value": "EC",
				"label": "ECUADOR"
			});
			countries.push(
			{
				"value": "EG",
				"label": "EGYPT"
			});
			countries.push(
			{
				"value": "SV",
				"label": "EL SALVADOR"
			});
			countries.push(
			{
				"value": "GQ",
				"label": "EQUATORIAL GUINEA"
			});
			countries.push(
			{
				"value": "ER",
				"label": "ERITREA"
			});
			countries.push(
			{
				"value": "EE",
				"label": "ESTONIA"
			});
			countries.push(
			{
				"value": "ET",
				"label": "ETHIOPIA"
			});
			countries.push(
			{
				"value": "FK",
				"label": "FALKLAND ISLANDS (MALVINAS)"
			});
			countries.push(
			{
				"value": "FO",
				"label": "FAROE ISLANDS"
			});
			countries.push(
			{
				"value": "FJ",
				"label": "FIJI"
			});
			countries.push(
			{
				"value": "FI",
				"label": "FINLAND"
			});
			countries.push(
			{
				"value": "FR",
				"label": "FRANCE"
			});
			countries.push(
			{
				"value": "GF",
				"label": "FRENCH GUIANA"
			});
			countries.push(
			{
				"value": "PF",
				"label": "FRENCH POLYNESIA"
			});
			countries.push(
			{
				"value": "TF",
				"label": "FRENCH SOUTHERN TERRITORIES"
			});
			countries.push(
			{
				"value": "GA",
				"label": "GABON"
			});
			countries.push(
			{
				"value": "GM",
				"label": "GAMBIA"
			});
			countries.push(
			{
				"value": "GE",
				"label": "GEORGIA"
			});
			countries.push(
			{
				"value": "DE",
				"label": "GERMANY"
			});
			countries.push(
			{
				"value": "GH",
				"label": "GHANA"
			});
			countries.push(
			{
				"value": "GI",
				"label": "GIBRALTAR"
			});
			countries.push(
			{
				"value": "GR",
				"label": "GREECE"
			});
			countries.push(
			{
				"value": "GL",
				"label": "GREENLAND"
			});
			countries.push(
			{
				"value": "GD",
				"label": "GRENADA"
			});
			countries.push(
			{
				"value": "GP",
				"label": "GUADELOUPE"
			});
			countries.push(
			{
				"value": "GU",
				"label": "GUAM"
			});
			countries.push(
			{
				"value": "GT",
				"label": "GUATEMALA"
			});
			countries.push(
			{
				"value": "GG",
				"label": "GUERNSEY"
			});
			countries.push(
			{
				"value": "GN",
				"label": "GUINEA"
			});
			countries.push(
			{
				"value": "GW",
				"label": "GUINEA-BISSAU"
			});
			countries.push(
			{
				"value": "GY",
				"label": "GUYANA"
			});
			countries.push(
			{
				"value": "HT",
				"label": "HAITI"
			});
			countries.push(
			{
				"value": "HM",
				"label": "HEARD ISLAND AND MCDONALD ISLANDS"
			});
			countries.push(
			{
				"value": "VA",
				"label": "HOLY SEE (VATICAN CITY STATE)"
			});
			countries.push(
			{
				"value": "HN",
				"label": "HONDURAS"
			});
			countries.push(
			{
				"value": "HK",
				"label": "HONG KONG"
			});
			countries.push(
			{
				"value": "HU",
				"label": "HUNGARY"
			});
			countries.push(
			{
				"value": "IS",
				"label": "ICELAND"
			});
			countries.push(
			{
				"value": "IN",
				"label": "INDIA"
			});
			countries.push(
			{
				"value": "ID",
				"label": "INDONESIA"
			});
			countries.push(
			{
				"value": "IR",
				"label": "IRAN, ISLAMIC REPUBLIC OF"
			});
			countries.push(
			{
				"value": "IQ",
				"label": "IRAQ"
			});
			countries.push(
			{
				"value": "IE",
				"label": "IRELAND"
			});
			countries.push(
			{
				"value": "IM",
				"label": "ISLE OF MAN"
			});
			countries.push(
			{
				"value": "IL",
				"label": "ISRAEL"
			});
			countries.push(
			{
				"value": "IT",
				"label": "ITALY"
			});
			countries.push(
			{
				"value": "JM",
				"label": "JAMAICA"
			});
			countries.push(
			{
				"value": "JP",
				"label": "JAPAN"
			});
			countries.push(
			{
				"value": "JE",
				"label": "JERSEY"
			});
			countries.push(
			{
				"value": "JO",
				"label": "JORDAN"
			});
			countries.push(
			{
				"value": "KZ",
				"label": "KAZAKHSTAN"
			});
			countries.push(
			{
				"value": "KE",
				"label": "KENYA"
			});
			countries.push(
			{
				"value": "KI",
				"label": "KIRIBATI"
			});
			countries.push(
			{
				"value": "KP",
				"label": "KOREA, DEMOCRATIC PEOPLES REPUBLIC OF"
			});
			countries.push(
			{
				"value": "KR",
				"label": "KOREA, REPUBLIC OF"
			});
			countries.push(
			{
				"value": "KW",
				"label": "KUWAIT"
			});
			countries.push(
			{
				"value": "KG",
				"label": "KYRGYZSTAN"
			});
			countries.push(
			{
				"value": "LA",
				"label": "LAO PEOPLES DEMOCRATIC REPUBLIC"
			});
			countries.push(
			{
				"value": "LV",
				"label": "LATVIA"
			});
			countries.push(
			{
				"value": "LB",
				"label": "LEBANON"
			});
			countries.push(
			{
				"value": "LS",
				"label": "LESOTHO"
			});
			countries.push(
			{
				"value": "LR",
				"label": "LIBERIA"
			});
			countries.push(
			{
				"value": "LY",
				"label": "LIBYAN ARAB JAMAHIRIYA"
			});
			countries.push(
			{
				"value": "LI",
				"label": "LIECHTENSTEIN"
			});
			countries.push(
			{
				"value": "LT",
				"label": "LITHUANIA"
			});
			countries.push(
			{
				"value": "LU",
				"label": "LUXEMBOURG"
			});
			countries.push(
			{
				"value": "MO",
				"label": "MACAO"
			});
			countries.push(
			{
				"value": "MK",
				"label": "MACEDONIA, THE FORMER YUGOSLAV REPUBLIC OF"
			});
			countries.push(
			{
				"value": "MG",
				"label": "MADAGASCAR"
			});
			countries.push(
			{
				"value": "MW",
				"label": "MALAWI"
			});
			countries.push(
			{
				"value": "MY",
				"label": "MALAYSIA"
			});
			countries.push(
			{
				"value": "MV",
				"label": "MALDIVES"
			});
			countries.push(
			{
				"value": "ML",
				"label": "MALI"
			});
			countries.push(
			{
				"value": "MT",
				"label": "MALTA"
			});
			countries.push(
			{
				"value": "MH",
				"label": "MARSHALL ISLANDS"
			});
			countries.push(
			{
				"value": "MQ",
				"label": "MARTINIQUE"
			});
			countries.push(
			{
				"value": "MR",
				"label": "MAURITANIA"
			});
			countries.push(
			{
				"value": "MU",
				"label": "MAURITIUS"
			});
			countries.push(
			{
				"value": "YT",
				"label": "MAYOTTE"
			});
			countries.push(
			{
				"value": "MX",
				"label": "MEXICO"
			});
			countries.push(
			{
				"value": "FM",
				"label": "MICRONESIA, FEDERATED STATES OF"
			});
			countries.push(
			{
				"value": "MD",
				"label": "MOLDOVA"
			});
			countries.push(
			{
				"value": "MC",
				"label": "MONACO"
			});
			countries.push(
			{
				"value": "MN",
				"label": "MONGOLIA"
			});
			countries.push(
			{
				"value": "ME",
				"label": "MONTENEGRO"
			});
			countries.push(
			{
				"value": "MS",
				"label": "MONTSERRAT"
			});
			countries.push(
			{
				"value": "MA",
				"label": "MOROCCO"
			});
			countries.push(
			{
				"value": "MZ",
				"label": "MOZAMBIQUE"
			});
			countries.push(
			{
				"value": "MM",
				"label": "MYANMAR"
			});
			countries.push(
			{
				"value": "NA",
				"label": "NAMIBIA"
			});
			countries.push(
			{
				"value": "NR",
				"label": "NAURU"
			});
			countries.push(
			{
				"value": "NP",
				"label": "NEPAL"
			});
			countries.push(
			{
				"value": "NL",
				"label": "NETHERLANDS"
			});
			countries.push(
			{
				"value": "AN",
				"label": "NETHERLANDS ANTILLES"
			});
			countries.push(
			{
				"value": "NC",
				"label": "NEW CALEDONIA"
			});
			countries.push(
			{
				"value": "NZ",
				"label": "NEW ZEALAND"
			});
			countries.push(
			{
				"value": "NI",
				"label": "NICARAGUA"
			});
			countries.push(
			{
				"value": "NE",
				"label": "NIGER"
			});
			countries.push(
			{
				"value": "NG",
				"label": "NIGERIA"
			});
			countries.push(
			{
				"value": "NU",
				"label": "NIUE"
			});
			countries.push(
			{
				"value": "NF",
				"label": "NORFOLK ISLAND"
			});
			countries.push(
			{
				"value": "MP",
				"label": "NORTHERN MARIANA ISLANDS"
			});
			countries.push(
			{
				"value": "NO",
				"label": "NORWAY"
			});
			countries.push(
			{
				"value": "OM",
				"label": "OMAN"
			});
			countries.push(
			{
				"value": "PK",
				"label": "PAKISTAN"
			});
			countries.push(
			{
				"value": "PW",
				"label": "PALAU"
			});
			countries.push(
			{
				"value": "PS",
				"label": "PALESTINIAN TERRITORY, OCCUPIED"
			});
			countries.push(
			{
				"value": "PA",
				"label": "PANAMA"
			});
			countries.push(
			{
				"value": "PG",
				"label": "PAPUA NEW GUINEA"
			});
			countries.push(
			{
				"value": "PY",
				"label": "PARAGUAY"
			});
			countries.push(
			{
				"value": "PE",
				"label": "PERU"
			});
			countries.push(
			{
				"value": "PH",
				"label": "PHILIPPINES"
			});
			countries.push(
			{
				"value": "PN",
				"label": "PITCAIRN"
			});
			countries.push(
			{
				"value": "PL",
				"label": "POLAND"
			});
			countries.push(
			{
				"value": "PT",
				"label": "PORTUGAL"
			});
			countries.push(
			{
				"value": "PR",
				"label": "PUERTO RICO"
			});
			countries.push(
			{
				"value": "QA",
				"label": "QATAR"
			});
			countries.push(
			{
				"value": "RE",
				"label": "RÆUNION"
			});
			countries.push(
			{
				"value": "RO",
				"label": "ROMANIA"
			});
			countries.push(
			{
				"value": "RU",
				"label": "RUSSIAN FEDERATION"
			});
			countries.push(
			{
				"value": "RW",
				"label": "RWANDA"
			});
			countries.push(
			{
				"value": "BL",
				"label": "SAINT BARTHÆLEMY"
			});
			countries.push(
			{
				"value": "SH",
				"label": "SAINT HELENA"
			});
			countries.push(
			{
				"value": "KN",
				"label": "SAINT KITTS AND NEVIS"
			});
			countries.push(
			{
				"value": "LC",
				"label": "SAINT LUCIA"
			});
			countries.push(
			{
				"value": "MF",
				"label": "SAINT MARTIN"
			});
			countries.push(
			{
				"value": "PM",
				"label": "SAINT PIERRE AND MIQUELON"
			});
			countries.push(
			{
				"value": "VC",
				"label": "SAINT VINCENT AND THE GRENADINES"
			});
			countries.push(
			{
				"value": "WS",
				"label": "SAMOA"
			});
			countries.push(
			{
				"value": "SM",
				"label": "SAN MARINO"
			});
			countries.push(
			{
				"value": "ST",
				"label": "SAO TOME AND PRINCIPE"
			});
			countries.push(
			{
				"value": "SA",
				"label": "SAUDI ARABIA"
			});
			countries.push(
			{
				"value": "SN",
				"label": "SENEGAL"
			});
			countries.push(
			{
				"value": "RS",
				"label": "SERBIA"
			});
			countries.push(
			{
				"value": "SC",
				"label": "SEYCHELLES"
			});
			countries.push(
			{
				"value": "SL",
				"label": "SIERRA LEONE"
			});
			countries.push(
			{
				"value": "SG",
				"label": "SINGAPORE"
			});
			countries.push(
			{
				"value": "SK",
				"label": "SLOVAKIA"
			});
			countries.push(
			{
				"value": "SI",
				"label": "SLOVENIA"
			});
			countries.push(
			{
				"value": "SB",
				"label": "SOLOMON ISLANDS"
			});
			countries.push(
			{
				"value": "SO",
				"label": "SOMALIA"
			});
			countries.push(
			{
				"value": "ZA",
				"label": "SOUTH AFRICA"
			});
			countries.push(
			{
				"value": "GS",
				"label": "SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS"
			});
			countries.push(
			{
				"value": "ES",
				"label": "SPAIN"
			});
			countries.push(
			{
				"value": "LK",
				"label": "SRI LANKA"
			});
			countries.push(
			{
				"value": "SD",
				"label": "SUDAN"
			});
			countries.push(
			{
				"value": "SR",
				"label": "SURINAME"
			});
			countries.push(
			{
				"value": "SJ",
				"label": "SVALBARD AND JAN MAYEN"
			});
			countries.push(
			{
				"value": "SZ",
				"label": "SWAZILAND"
			});
			countries.push(
			{
				"value": "SE",
				"label": "SWEDEN"
			});
			countries.push(
			{
				"value": "CH",
				"label": "SWITZERLAND"
			});
			countries.push(
			{
				"value": "SY",
				"label": "SYRIAN ARAB REPUBLIC"
			});
			countries.push(
			{
				"value": "TW",
				"label": "TAIWAN, PROVINCE OF CHINA"
			});
			countries.push(
			{
				"value": "TJ",
				"label": "TAJIKISTAN"
			});
			countries.push(
			{
				"value": "TZ",
				"label": "TANZANIA, UNITED REPUBLIC OF"
			});
			countries.push(
			{
				"value": "TH",
				"label": "THAILAND"
			});
			countries.push(
			{
				"value": "TL",
				"label": "TIMOR-LESTE"
			});
			countries.push(
			{
				"value": "TG",
				"label": "TOGO"
			});
			countries.push(
			{
				"value": "TK",
				"label": "TOKELAU"
			});
			countries.push(
			{
				"value": "TO",
				"label": "TONGA"
			});
			countries.push(
			{
				"value": "TT",
				"label": "TRINIDAD AND TOBAGO"
			});
			countries.push(
			{
				"value": "TN",
				"label": "TUNISIA"
			});
			countries.push(
			{
				"value": "TR",
				"label": "TURKEY"
			});
			countries.push(
			{
				"value": "TM",
				"label": "TURKMENISTAN"
			});
			countries.push(
			{
				"value": "TC",
				"label": "TURKS AND CAICOS ISLANDS"
			});
			countries.push(
			{
				"value": "TV",
				"label": "TUVALU"
			});
			countries.push(
			{
				"value": "UG",
				"label": "UGANDA"
			});
			countries.push(
			{
				"value": "UA",
				"label": "UKRAINE"
			});
			countries.push(
			{
				"value": "AE",
				"label": "UNITED ARAB EMIRATES"
			});
			countries.push(
			{
				"value": "GB",
				"label": "UNITED KINGDOM"
			});
			countries.push(
			{
				"value": "US",
				"label": "UNITED STATES"
			});
			countries.push(
			{
				"value": "UM",
				"label": "UNITED STATES MINOR OUTLYING ISLANDS"
			});
			countries.push(
			{
				"value": "UY",
				"label": "URUGUAY"
			});
			countries.push(
			{
				"value": "UZ",
				"label": "UZBEKISTAN"
			});
			countries.push(
			{
				"value": "VU",
				"label": "VANUATU"
			});
			countries.push(
			{
				"value": "VA",
				"label": "VATICAN CITY STATE"
			});
			countries.push(
			{
				"value": "VE",
				"label": "VENEZUELA"
			});
			countries.push(
			{
				"value": "VN",
				"label": "VIET NAM"
			});
			countries.push(
			{
				"value": "VG",
				"label": "VIRGIN ISLANDS, BRITISH"
			});
			countries.push(
			{
				"value": "VI",
				"label": "VIRGIN ISLANDS, U.S."
			});
			countries.push(
			{
				"value": "WF",
				"label": "WALLIS AND FUTUNA"
			});
			countries.push(
			{
				"value": "EH",
				"label": "WESTERN SAHARA"
			});
			countries.push(
			{
				"value": "YE",
				"label": "YEMEN"
			});
			countries.push(
			{
				"value": "ZM",
				"label": "ZAMBIA"
			});
			countries.push(
			{
				"value": "ZW",
				"label": "ZIMBABWE"
			});
			return countries;
		};

		service.getEngFre = function getEngFre()
		{
			var langs = [];
			langs.push(
			{
				"value": "English",
				"label": "English"
			});
			langs.push(
			{
				"value": "French",
				"label": "French"
			});
			langs.push(
			{
				"value": "Other",
				"label": "Other"
			});
			return langs;
		};

		service.getSpokenLanguages = function getSpokenLanguages()
		{
			var langs = [];
			langs.push(
			{
				"value": "English",
				"label": "English"
			});
			langs.push(
			{
				"value": "French",
				"label": "French"
			});
			langs.push(
			{
				"value": "Abkhazian",
				"label": "Abkhazian"
			});
			langs.push(
			{
				"value": "Achinese",
				"label": "Achinese"
			});
			langs.push(
			{
				"value": "Acoli",
				"label": "Acoli"
			});
			langs.push(
			{
				"value": "Adangme",
				"label": "Adangme"
			});
			langs.push(
			{
				"value": "Adyghe",
				"label": "Adyghe"
			});
			langs.push(
			{
				"value": "Afar",
				"label": "Afar"
			});
			langs.push(
			{
				"value": "Afrihili",
				"label": "Afrihili"
			});
			langs.push(
			{
				"value": "Afrikaans",
				"label": "Afrikaans"
			});
			langs.push(
			{
				"value": "Afro-Asiatic (Other)",
				"label": "Afro-Asiatic"
			});
			langs.push(
			{
				"value": "Ainu",
				"label": "Ainu"
			});
			langs.push(
			{
				"value": "Akan",
				"label": "Akan"
			});
			langs.push(
			{
				"value": "Akkadian",
				"label": "Akkadian"
			});
			langs.push(
			{
				"value": "Albanian",
				"label": "Albanian"
			});
			langs.push(
			{
				"value": "Aleut",
				"label": "Aleut"
			});
			langs.push(
			{
				"value": "Algonquian Languages",
				"label": "Algonquian Languages"
			});
			langs.push(
			{
				"value": "Altaic (Other)",
				"label": "Altaic"
			});
			langs.push(
			{
				"value": "Amharic",
				"label": "Amharic"
			});
			langs.push(
			{
				"value": "Angika",
				"label": "Angika"
			});
			langs.push(
			{
				"value": "Apache Languages",
				"label": "Apache Languages"
			});
			langs.push(
			{
				"value": "Arabic",
				"label": "Arabic"
			});
			langs.push(
			{
				"value": "Aramaic",
				"label": "Aramaic"
			});
			langs.push(
			{
				"value": "Arapaho",
				"label": "Arapaho"
			});
			langs.push(
			{
				"value": "Araucanian",
				"label": "Araucanian"
			});
			langs.push(
			{
				"value": "Arawak",
				"label": "Arawak"
			});
			langs.push(
			{
				"value": "Argonese",
				"label": "Argonese"
			});
			langs.push(
			{
				"value": "Armenian",
				"label": "Armenian"
			});
			langs.push(
			{
				"value": "Aromanian",
				"label": "Aromanian"
			});
			langs.push(
			{
				"value": "Artificial (Other)",
				"label": "Artificial"
			});
			langs.push(
			{
				"value": "Assamese",
				"label": "Assamese"
			});
			langs.push(
			{
				"value": "Asturian",
				"label": "Asturian"
			});
			langs.push(
			{
				"value": "Athapascan Languages",
				"label": "Athapascan Languages"
			});
			langs.push(
			{
				"value": "Australian Languages",
				"label": "Australian Languages"
			});
			langs.push(
			{
				"value": "Austronesian (Other)",
				"label": "Austronesian"
			});
			langs.push(
			{
				"value": "Avaric",
				"label": "Avaric"
			});
			langs.push(
			{
				"value": "Avestan",
				"label": "Avestan"
			});
			langs.push(
			{
				"value": "Awadhi",
				"label": "Awadhi"
			});
			langs.push(
			{
				"value": "Aymara",
				"label": "Aymara"
			});
			langs.push(
			{
				"value": "Azerbaijani",
				"label": "Azerbaijani"
			});
			langs.push(
			{
				"value": "Balinese",
				"label": "Balinese"
			});
			langs.push(
			{
				"value": "Baltic (Other)",
				"label": "Baltic"
			});
			langs.push(
			{
				"value": "Baluchi",
				"label": "Baluchi"
			});
			langs.push(
			{
				"value": "Bambara",
				"label": "Bambara"
			});
			langs.push(
			{
				"value": "Bamileke Languages",
				"label": "Bamileke Languages"
			});
			langs.push(
			{
				"value": "Banda",
				"label": "Banda"
			});
			langs.push(
			{
				"value": "Bantu (Other)",
				"label": "Bantu"
			});
			langs.push(
			{
				"value": "Basa",
				"label": "Basa"
			});
			langs.push(
			{
				"value": "Bashkir",
				"label": "Bashkir"
			});
			langs.push(
			{
				"value": "Basque",
				"label": "Basque"
			});
			langs.push(
			{
				"value": "Batak (Indonesia)",
				"label": "Batak (Indonesia)"
			});
			langs.push(
			{
				"value": "Beja",
				"label": "Beja"
			});
			langs.push(
			{
				"value": "Belarusian",
				"label": "Belarusian"
			});
			langs.push(
			{
				"value": "Bemba",
				"label": "Bemba"
			});
			langs.push(
			{
				"value": "Bengali",
				"label": "Bengali"
			});
			langs.push(
			{
				"value": "Berber (Other)",
				"label": "Berber"
			});
			langs.push(
			{
				"value": "Bhojpuri",
				"label": "Bhojpuri"
			});
			langs.push(
			{
				"value": "Bihari",
				"label": "Bihari"
			});
			langs.push(
			{
				"value": "Bikol",
				"label": "Bikol"
			});
			langs.push(
			{
				"value": "Bini",
				"label": "Bini"
			});
			langs.push(
			{
				"value": "Bislama",
				"label": "Bislama"
			});
			langs.push(
			{
				"value": "Blin",
				"label": "Blin"
			});
			langs.push(
			{
				"value": "Bokmal, Norwegian",
				"label": "Bokmal, Norwegian"
			});
			langs.push(
			{
				"value": "Bosnian",
				"label": "Bosnian"
			});
			langs.push(
			{
				"value": "Braj",
				"label": "Braj"
			});
			langs.push(
			{
				"value": "Breton",
				"label": "Breton"
			});
			langs.push(
			{
				"value": "Buginese",
				"label": "Buginese"
			});
			langs.push(
			{
				"value": "Bulgarian",
				"label": "Bulgarian"
			});
			langs.push(
			{
				"value": "Buriat",
				"label": "Buriat"
			});
			langs.push(
			{
				"value": "Burmese",
				"label": "Burmese"
			});
			langs.push(
			{
				"value": "Caddo",
				"label": "Caddo"
			});
			langs.push(
			{
				"value": "Cantonese",
				"label": "Cantonese"
			});
			langs.push(
			{
				"value": "Carib",
				"label": "Carib"
			});
			langs.push(
			{
				"value": "Catalan",
				"label": "Catalan"
			});
			langs.push(
			{
				"value": "Caucasian (Other)",
				"label": "Caucasian"
			});
			langs.push(
			{
				"value": "Cebuano",
				"label": "Cebuano"
			});
			langs.push(
			{
				"value": "Celtic (Other)",
				"label": "Celtic"
			});
			langs.push(
			{
				"value": "Central American Indian (Other)",
				"label": "Central American Indian"
			});
			langs.push(
			{
				"value": "Chagatai",
				"label": "Chagatai"
			});
			langs.push(
			{
				"value": "Chamic Languages",
				"label": "Chamic Languages"
			});
			langs.push(
			{
				"value": "Chamorro",
				"label": "Chamorro"
			});
			langs.push(
			{
				"value": "Chechen",
				"label": "Chechen"
			});
			langs.push(
			{
				"value": "Cherokee",
				"label": "Cherokee"
			});
			langs.push(
			{
				"value": "Cheyenne",
				"label": "Cheyenne"
			});
			langs.push(
			{
				"value": "Chibcha",
				"label": "Chibcha"
			});
			langs.push(
			{
				"value": "Chichewa",
				"label": "Chichewa"
			});
			langs.push(
			{
				"value": "Chinese",
				"label": "Chinese"
			});
			langs.push(
			{
				"value": "Chinook Jargon",
				"label": "Chinook Jargon"
			});
			langs.push(
			{
				"value": "Chipewyan",
				"label": "Chipewyan"
			});
			langs.push(
			{
				"value": "Choctaw",
				"label": "Choctaw"
			});
			langs.push(
			{
				"value": "Chuukese",
				"label": "Chuukese"
			});
			langs.push(
			{
				"value": "Chuvash",
				"label": "Chuvash"
			});
			langs.push(
			{
				"value": "Classical Nepal Bhasa",
				"label": "Classical Nepal Bhasa"
			});
			langs.push(
			{
				"value": "Coptic",
				"label": "Coptic"
			});
			langs.push(
			{
				"value": "Cornish",
				"label": "Cornish"
			});
			langs.push(
			{
				"value": "Corsican",
				"label": "Corsican"
			});
			langs.push(
			{
				"value": "Cree",
				"label": "Cree"
			});
			langs.push(
			{
				"value": "Creek",
				"label": "Creek"
			});
			langs.push(
			{
				"value": "Creoles And Pidgins (Other)",
				"label": "Creoles & Pidgins"
			});
			langs.push(
			{
				"value": "Creoles And Pidgins, English-Based (Other)",
				"label": "Creoles & Pidgins, ENG-Based"
			});
			langs.push(
			{
				"value": "Creoles And Pidgins, French-Based (Other)",
				"label": "Creoles & Pidgins, FRE-Based"
			});
			langs.push(
			{
				"value": "Creoles And Pidgins, Portuguese-Based (Other)",
				"label": "Creoles & Pidgins, POR-Based"
			});
			langs.push(
			{
				"value": "Crimean Tatar",
				"label": "Crimean Tatar"
			});
			langs.push(
			{
				"value": "Croatian",
				"label": "Croatian"
			});
			langs.push(
			{
				"value": "Cushitic (Other)",
				"label": "Cushitic"
			});
			langs.push(
			{
				"value": "Czech",
				"label": "Czech"
			});
			langs.push(
			{
				"value": "Dakota",
				"label": "Dakota"
			});
			langs.push(
			{
				"value": "Danish",
				"label": "Danish"
			});
			langs.push(
			{
				"value": "Dargwa",
				"label": "Dargwa"
			});
			langs.push(
			{
				"value": "Dayak",
				"label": "Dayak"
			});
			langs.push(
			{
				"value": "Delaware",
				"label": "Delaware"
			});
			langs.push(
			{
				"value": "Dhivehi",
				"label": "Dhivehi"
			});
			langs.push(
			{
				"value": "Dinka",
				"label": "Dinka"
			});
			langs.push(
			{
				"value": "Dogri",
				"label": "Dogri"
			});
			langs.push(
			{
				"value": "Dogrib",
				"label": "Dogrib"
			});
			langs.push(
			{
				"value": "Dravidian (Other)",
				"label": "Dravidian"
			});
			langs.push(
			{
				"value": "Duala",
				"label": "Duala"
			});
			langs.push(
			{
				"value": "Dutch",
				"label": "Dutch"
			});
			langs.push(
			{
				"value": "Dyula",
				"label": "Dyula"
			});
			langs.push(
			{
				"value": "Dzongkha",
				"label": "Dzongkha"
			});
			langs.push(
			{
				"value": "Eastern Frisian",
				"label": "Eastern Frisian"
			});
			langs.push(
			{
				"value": "Efik",
				"label": "Efik"
			});
			langs.push(
			{
				"value": "Egyptian (Ancient)",
				"label": "Egyptian (Ancient)"
			});
			langs.push(
			{
				"value": "Ekajuk",
				"label": "Ekajuk"
			});
			langs.push(
			{
				"value": "Elamite",
				"label": "Elamite"
			});
			langs.push(
			{
				"value": "Erzya",
				"label": "Erzya"
			});
			langs.push(
			{
				"value": "Esperanto",
				"label": "Esperanto"
			});
			langs.push(
			{
				"value": "Estonian",
				"label": "Estonian"
			});
			langs.push(
			{
				"value": "Ewe",
				"label": "Ewe"
			});
			langs.push(
			{
				"value": "Ewondo",
				"label": "Ewondo"
			});
			langs.push(
			{
				"value": "Fang",
				"label": "Fang"
			});
			langs.push(
			{
				"value": "Fanti",
				"label": "Fanti"
			});
			langs.push(
			{
				"value": "Faroese",
				"label": "Faroese"
			});
			langs.push(
			{
				"value": "Fijian",
				"label": "Fijian"
			});
			langs.push(
			{
				"value": "Filipino; Pilipino",
				"label": "Filipino; Pilipino"
			});
			langs.push(
			{
				"value": "Finnish",
				"label": "Finnish"
			});
			langs.push(
			{
				"value": "Finno-Ugrian (Other)",
				"label": "Finno-Ugrian"
			});
			langs.push(
			{
				"value": "Fon",
				"label": "Fon"
			});
			langs.push(
			{
				"value": "Friulian",
				"label": "Friulian"
			});
			langs.push(
			{
				"value": "Fulah",
				"label": "Fulah"
			});
			langs.push(
			{
				"value": "Ga",
				"label": "Ga"
			});
			langs.push(
			{
				"value": "Gaelic",
				"label": "Gaelic"
			});
			langs.push(
			{
				"value": "Gallegan",
				"label": "Gallegan"
			});
			langs.push(
			{
				"value": "Ganda",
				"label": "Ganda"
			});
			langs.push(
			{
				"value": "Gayo",
				"label": "Gayo"
			});
			langs.push(
			{
				"value": "Gbaya",
				"label": "Gbaya"
			});
			langs.push(
			{
				"value": "Geez",
				"label": "Geez"
			});
			langs.push(
			{
				"value": "Georgian",
				"label": "Georgian"
			});
			langs.push(
			{
				"value": "German",
				"label": "German"
			});
			langs.push(
			{
				"value": "Germanic (Other)",
				"label": "Germanic"
			});
			langs.push(
			{
				"value": "Gikuyu",
				"label": "Gikuyu"
			});
			langs.push(
			{
				"value": "Gilbertese",
				"label": "Gilbertese"
			});
			langs.push(
			{
				"value": "Gondi",
				"label": "Gondi"
			});
			langs.push(
			{
				"value": "Gorontalo",
				"label": "Gorontalo"
			});
			langs.push(
			{
				"value": "Gothic",
				"label": "Gothic"
			});
			langs.push(
			{
				"value": "Grebo",
				"label": "Grebo"
			});
			langs.push(
			{
				"value": "Greek, Modern (1453-)",
				"label": "Greek, Modern (1453-)"
			});
			langs.push(
			{
				"value": "Guarani",
				"label": "Guarani"
			});
			langs.push(
			{
				"value": "Gujarati",
				"label": "Gujarati"
			});
			langs.push(
			{
				"value": "Gwich'in",
				"label": "Gwich'in"
			});
			langs.push(
			{
				"value": "Haida",
				"label": "Haida"
			});
			langs.push(
			{
				"value": "Haitian Creole",
				"label": "Haitian Creole"
			});
			langs.push(
			{
				"value": "Hausa",
				"label": "Hausa"
			});
			langs.push(
			{
				"value": "Hawaiian",
				"label": "Hawaiian"
			});
			langs.push(
			{
				"value": "Hebrew",
				"label": "Hebrew"
			});
			langs.push(
			{
				"value": "Herero",
				"label": "Herero"
			});
			langs.push(
			{
				"value": "Hiligaynon",
				"label": "Hiligaynon"
			});
			langs.push(
			{
				"value": "Himachali",
				"label": "Himachali"
			});
			langs.push(
			{
				"value": "Hindi",
				"label": "Hindi"
			});
			langs.push(
			{
				"value": "Hiri Motu",
				"label": "Hiri Motu"
			});
			langs.push(
			{
				"value": "Hittite",
				"label": "Hittite"
			});
			langs.push(
			{
				"value": "Hmong",
				"label": "Hmong"
			});
			langs.push(
			{
				"value": "Hungarian",
				"label": "Hungarian"
			});
			langs.push(
			{
				"value": "Hupa",
				"label": "Hupa"
			});
			langs.push(
			{
				"value": "Iban",
				"label": "Iban"
			});
			langs.push(
			{
				"value": "Icelandic",
				"label": "Icelandic"
			});
			langs.push(
			{
				"value": "Ido",
				"label": "Ido"
			});
			langs.push(
			{
				"value": "Igbo",
				"label": "Igbo"
			});
			langs.push(
			{
				"value": "Ijo",
				"label": "Ijo"
			});
			langs.push(
			{
				"value": "Iloko",
				"label": "Iloko"
			});
			langs.push(
			{
				"value": "Inari Sami",
				"label": "Inari Sami"
			});
			langs.push(
			{
				"value": "Indic (Other)",
				"label": "Indic"
			});
			langs.push(
			{
				"value": "Indo-European (Other)",
				"label": "Indo-European"
			});
			langs.push(
			{
				"value": "Indonesian",
				"label": "Indonesian"
			});
			langs.push(
			{
				"value": "Ingush",
				"label": "Ingush"
			});
			langs.push(
			{
				"value": "Interlingua (International Auxiliary Lang. Assoc.)",
				"label": "Interlingua (IALA)"
			});
			langs.push(
			{
				"value": "Interlingue",
				"label": "Interlingue"
			});
			langs.push(
			{
				"value": "Inuktitut",
				"label": "Inuktitut"
			});
			langs.push(
			{
				"value": "Inupiaq",
				"label": "Inupiaq"
			});
			langs.push(
			{
				"value": "Iranian (Other)",
				"label": "Iranian"
			});
			langs.push(
			{
				"value": "Irish",
				"label": "Irish"
			});
			langs.push(
			{
				"value": "Iroquoian Languages",
				"label": "Iroquoian Languages"
			});
			langs.push(
			{
				"value": "Italian",
				"label": "Italian"
			});
			langs.push(
			{
				"value": "Japanese",
				"label": "Japanese"
			});
			langs.push(
			{
				"value": "Javanese",
				"label": "Javanese"
			});
			langs.push(
			{
				"value": "Judeo-Arabic",
				"label": "Judeo-Arabic"
			});
			langs.push(
			{
				"value": "Judeo-Persian",
				"label": "Judeo-Persian"
			});
			langs.push(
			{
				"value": "Kabardian",
				"label": "Kabardian"
			});
			langs.push(
			{
				"value": "Kabyle",
				"label": "Kabyle"
			});
			langs.push(
			{
				"value": "Kachin",
				"label": "Kachin"
			});
			langs.push(
			{
				"value": "Kalaallisut",
				"label": "Kalaallisut"
			});
			langs.push(
			{
				"value": "Kalmyk",
				"label": "Kalmyk"
			});
			langs.push(
			{
				"value": "Kamba",
				"label": "Kamba"
			});
			langs.push(
			{
				"value": "Kannada",
				"label": "Kannada"
			});
			langs.push(
			{
				"value": "Kanuri",
				"label": "Kanuri"
			});
			langs.push(
			{
				"value": "Kara-Kalpak",
				"label": "Kara-Kalpak"
			});
			langs.push(
			{
				"value": "Karachay-Balkar",
				"label": "Karachay-Balkar"
			});
			langs.push(
			{
				"value": "Karelian",
				"label": "Karelian"
			});
			langs.push(
			{
				"value": "Karen",
				"label": "Karen"
			});
			langs.push(
			{
				"value": "Kashmiri",
				"label": "Kashmiri"
			});
			langs.push(
			{
				"value": "Kashubian",
				"label": "Kashubian"
			});
			langs.push(
			{
				"value": "Kawi",
				"label": "Kawi"
			});
			langs.push(
			{
				"value": "Kazakh",
				"label": "Kazakh"
			});
			langs.push(
			{
				"value": "Khasi",
				"label": "Khasi"
			});
			langs.push(
			{
				"value": "Khmer",
				"label": "Khmer"
			});
			langs.push(
			{
				"value": "Khoisan (Other)",
				"label": "Khoisan"
			});
			langs.push(
			{
				"value": "Khotanese",
				"label": "Khotanese"
			});
			langs.push(
			{
				"value": "Kimbundu",
				"label": "Kimbundu"
			});
			langs.push(
			{
				"value": "Kinyarwanda",
				"label": "Kinyarwanda"
			});
			langs.push(
			{
				"value": "Kirghiz",
				"label": "Kirghiz"
			});
			langs.push(
			{
				"value": "Klingon",
				"label": "Klingon"
			});
			langs.push(
			{
				"value": "Komi",
				"label": "Komi"
			});
			langs.push(
			{
				"value": "Kongo",
				"label": "Kongo"
			});
			langs.push(
			{
				"value": "Konkani",
				"label": "Konkani"
			});
			langs.push(
			{
				"value": "Korean",
				"label": "Korean"
			});
			langs.push(
			{
				"value": "Kosraean",
				"label": "Kosraean"
			});
			langs.push(
			{
				"value": "Kpelle",
				"label": "Kpelle"
			});
			langs.push(
			{
				"value": "Kru",
				"label": "Kru"
			});
			langs.push(
			{
				"value": "Kumyk",
				"label": "Kumyk"
			});
			langs.push(
			{
				"value": "Kurdish",
				"label": "Kurdish"
			});
			langs.push(
			{
				"value": "Kurukh",
				"label": "Kurukh"
			});
			langs.push(
			{
				"value": "Kutenai",
				"label": "Kutenai"
			});
			langs.push(
			{
				"value": "Kwanyama",
				"label": "Kwanyama"
			});
			langs.push(
			{
				"value": "Ladino",
				"label": "Ladino"
			});
			langs.push(
			{
				"value": "Lahnda",
				"label": "Lahnda"
			});
			langs.push(
			{
				"value": "Lamba",
				"label": "Lamba"
			});
			langs.push(
			{
				"value": "Lao",
				"label": "Lao"
			});
			langs.push(
			{
				"value": "Latin",
				"label": "Latin"
			});
			langs.push(
			{
				"value": "Latvian",
				"label": "Latvian"
			});
			langs.push(
			{
				"value": "Lezghian",
				"label": "Lezghian"
			});
			langs.push(
			{
				"value": "Limburgish",
				"label": "Limburgish"
			});
			langs.push(
			{
				"value": "Lingala",
				"label": "Lingala"
			});
			langs.push(
			{
				"value": "Lithuanian",
				"label": "Lithuanian"
			});
			langs.push(
			{
				"value": "Lojban",
				"label": "Lojban"
			});
			langs.push(
			{
				"value": "Low German",
				"label": "Low German"
			});
			langs.push(
			{
				"value": "Lower Sorbian",
				"label": "Lower Sorbian"
			});
			langs.push(
			{
				"value": "Lozi",
				"label": "Lozi"
			});
			langs.push(
			{
				"value": "Luba-Katanga",
				"label": "Luba-Katanga"
			});
			langs.push(
			{
				"value": "Luba-Lulua",
				"label": "Luba-Lulua"
			});
			langs.push(
			{
				"value": "Luiseno",
				"label": "Luiseno"
			});
			langs.push(
			{
				"value": "Lule Sami",
				"label": "Lule Sami"
			});
			langs.push(
			{
				"value": "Lunda",
				"label": "Lunda"
			});
			langs.push(
			{
				"value": "Luo (Kenya And Tanzania)",
				"label": "Luo (Kenya & Tanzania)"
			});
			langs.push(
			{
				"value": "Lushai",
				"label": "Lushai"
			});
			langs.push(
			{
				"value": "Luxembourgish",
				"label": "Luxembourgish"
			});
			langs.push(
			{
				"value": "Macedonian",
				"label": "Macedonian"
			});
			langs.push(
			{
				"value": "Madurese",
				"label": "Madurese"
			});
			langs.push(
			{
				"value": "Magahi",
				"label": "Magahi"
			});
			langs.push(
			{
				"value": "Maithili",
				"label": "Maithili"
			});
			langs.push(
			{
				"value": "Makasar",
				"label": "Makasar"
			});
			langs.push(
			{
				"value": "Malagasy",
				"label": "Malagasy"
			});
			langs.push(
			{
				"value": "Malay",
				"label": "Malay"
			});
			langs.push(
			{
				"value": "Malayalam",
				"label": "Malayalam"
			});
			langs.push(
			{
				"value": "Maltese",
				"label": "Maltese"
			});
			langs.push(
			{
				"value": "Manchu",
				"label": "Manchu"
			});
			langs.push(
			{
				"value": "Mandar",
				"label": "Mandar"
			});
			langs.push(
			{
				"value": "Mandarin",
				"label": "Mandarin"
			});
			langs.push(
			{
				"value": "Mandingo",
				"label": "Mandingo"
			});
			langs.push(
			{
				"value": "Manipuri",
				"label": "Manipuri"
			});
			langs.push(
			{
				"value": "Manobo Languages",
				"label": "Manobo Languages"
			});
			langs.push(
			{
				"value": "Manx",
				"label": "Manx"
			});
			langs.push(
			{
				"value": "Maori",
				"label": "Maori"
			});
			langs.push(
			{
				"value": "Marathi",
				"label": "Marathi"
			});
			langs.push(
			{
				"value": "Mari",
				"label": "Mari"
			});
			langs.push(
			{
				"value": "Marshall",
				"label": "Marshall"
			});
			langs.push(
			{
				"value": "Marwari",
				"label": "Marwari"
			});
			langs.push(
			{
				"value": "Masai",
				"label": "Masai"
			});
			langs.push(
			{
				"value": "Mayan Languages",
				"label": "Mayan Languages"
			});
			langs.push(
			{
				"value": "Mende",
				"label": "Mende"
			});
			langs.push(
			{
				"value": "Micmac",
				"label": "Micmac"
			});
			langs.push(
			{
				"value": "Minangkabau",
				"label": "Minangkabau"
			});
			langs.push(
			{
				"value": "Mirandese",
				"label": "Mirandese"
			});
			langs.push(
			{
				"value": "Miscellaneous Languages",
				"label": "Miscellaneous Languages"
			});
			langs.push(
			{
				"value": "Mohawk",
				"label": "Mohawk"
			});
			langs.push(
			{
				"value": "Moksha",
				"label": "Moksha"
			});
			langs.push(
			{
				"value": "Moldavian",
				"label": "Moldavian"
			});
			langs.push(
			{
				"value": "Mon-Khmer (Other)",
				"label": "Mon-Khmer"
			});
			langs.push(
			{
				"value": "Mongo",
				"label": "Mongo"
			});
			langs.push(
			{
				"value": "Mongolian",
				"label": "Mongolian"
			});
			langs.push(
			{
				"value": "Mossi",
				"label": "Mossi"
			});
			langs.push(
			{
				"value": "Multiple Languages",
				"label": "Multiple Languages"
			});
			langs.push(
			{
				"value": "Munda Languages",
				"label": "Munda Languages"
			});
			langs.push(
			{
				"value": "N'Ko",
				"label": "N'Ko"
			});
			langs.push(
			{
				"value": "Nahuatl",
				"label": "Nahuatl"
			});
			langs.push(
			{
				"value": "Nauru",
				"label": "Nauru"
			});
			langs.push(
			{
				"value": "Navajo",
				"label": "Navajo"
			});
			langs.push(
			{
				"value": "Ndonga",
				"label": "Ndonga"
			});
			langs.push(
			{
				"value": "Neapolitan",
				"label": "Neapolitan"
			});
			langs.push(
			{
				"value": "Nepal Bhasa",
				"label": "Nepal Bhasa"
			});
			langs.push(
			{
				"value": "Nepali",
				"label": "Nepali"
			});
			langs.push(
			{
				"value": "Nias",
				"label": "Nias"
			});
			langs.push(
			{
				"value": "Niger-Kordofanian (Other)",
				"label": "Niger-Kordofanian"
			});
			langs.push(
			{
				"value": "Nilo-Saharan (Other)",
				"label": "Nilo-Saharan"
			});
			langs.push(
			{
				"value": "Niuean",
				"label": "Niuean"
			});
			langs.push(
			{
				"value": "Nogai",
				"label": "Nogai"
			});
			langs.push(
			{
				"value": "Norse, Old",
				"label": "Norse, Old"
			});
			langs.push(
			{
				"value": "North American Indian (Other)",
				"label": "North American Indian"
			});
			langs.push(
			{
				"value": "North Ndebele",
				"label": "North Ndebele"
			});
			langs.push(
			{
				"value": "Northern Frisian",
				"label": "Northern Frisian"
			});
			langs.push(
			{
				"value": "Northern Sami",
				"label": "Northern Sami"
			});
			langs.push(
			{
				"value": "Northern Sotho",
				"label": "Northern Sotho"
			});
			langs.push(
			{
				"value": "Norwegian",
				"label": "Norwegian"
			});
			langs.push(
			{
				"value": "Norwegian Nynorsk",
				"label": "Norwegian Nynorsk"
			});
			langs.push(
			{
				"value": "Nubian Languages",
				"label": "Nubian Languages"
			});
			langs.push(
			{
				"value": "Nyamwezi",
				"label": "Nyamwezi"
			});
			langs.push(
			{
				"value": "Nyankole",
				"label": "Nyankole"
			});
			langs.push(
			{
				"value": "Nyoro",
				"label": "Nyoro"
			});
			langs.push(
			{
				"value": "Nzima",
				"label": "Nzima"
			});
			langs.push(
			{
				"value": "Occitan (Post 1500)",
				"label": "Occitan (Post 1500)"
			});
			langs.push(
			{
				"value": "Ojibwa",
				"label": "Ojibwa"
			});
			langs.push(
			{
				"value": "Old Church Slavonic",
				"label": "Old Church Slavonic"
			});
			langs.push(
			{
				"value": "Oriya",
				"label": "Oriya"
			});
			langs.push(
			{
				"value": "Oromo",
				"label": "Oromo"
			});
			langs.push(
			{
				"value": "Osage",
				"label": "Osage"
			});
			langs.push(
			{
				"value": "Ossetic",
				"label": "Ossetic"
			});
			langs.push(
			{
				"value": "Otomian Languages",
				"label": "Otomian Languages"
			});
			langs.push(
			{
				"value": "Pahlavi",
				"label": "Pahlavi"
			});
			langs.push(
			{
				"value": "Palauan",
				"label": "Palauan"
			});
			langs.push(
			{
				"value": "Pali",
				"label": "Pali"
			});
			langs.push(
			{
				"value": "Pampanga",
				"label": "Pampanga"
			});
			langs.push(
			{
				"value": "Pangasinan",
				"label": "Pangasinan"
			});
			langs.push(
			{
				"value": "Papiamento",
				"label": "Papiamento"
			});
			langs.push(
			{
				"value": "Papuan (Other)",
				"label": "Papuan"
			});
			langs.push(
			{
				"value": "Persian",
				"label": "Persian"
			});
			langs.push(
			{
				"value": "Philippine (Other)",
				"label": "Philippine"
			});
			langs.push(
			{
				"value": "Phoenician",
				"label": "Phoenician"
			});
			langs.push(
			{
				"value": "Pohnpeian",
				"label": "Pohnpeian"
			});
			langs.push(
			{
				"value": "Polish",
				"label": "Polish"
			});
			langs.push(
			{
				"value": "Portuguese",
				"label": "Portuguese"
			});
			langs.push(
			{
				"value": "Prakrit Languages",
				"label": "Prakrit Languages"
			});
			langs.push(
			{
				"value": "Punjabi",
				"label": "Punjabi"
			});
			langs.push(
			{
				"value": "Pushto",
				"label": "Pushto"
			});
			langs.push(
			{
				"value": "Quechua",
				"label": "Quechua"
			});
			langs.push(
			{
				"value": "Raeto-Romance",
				"label": "Raeto-Romance"
			});
			langs.push(
			{
				"value": "Rajasthani",
				"label": "Rajasthani"
			});
			langs.push(
			{
				"value": "Rapanui",
				"label": "Rapanui"
			});
			langs.push(
			{
				"value": "Rarotongan",
				"label": "Rarotongan"
			});
			langs.push(
			{
				"value": "Romance (Other)",
				"label": "Romance"
			});
			langs.push(
			{
				"value": "Romanian",
				"label": "Romanian"
			});
			langs.push(
			{
				"value": "Romany",
				"label": "Romany"
			});
			langs.push(
			{
				"value": "Rundi",
				"label": "Rundi"
			});
			langs.push(
			{
				"value": "Russian",
				"label": "Russian"
			});
			langs.push(
			{
				"value": "Salishan Languages",
				"label": "Salishan Languages"
			});
			langs.push(
			{
				"value": "Samaritan Aramaic",
				"label": "Samaritan Aramaic"
			});
			langs.push(
			{
				"value": "Sami Languages",
				"label": "Sami Languages"
			});
			langs.push(
			{
				"value": "Samoan",
				"label": "Samoan"
			});
			langs.push(
			{
				"value": "Sandawe",
				"label": "Sandawe"
			});
			langs.push(
			{
				"value": "Sango",
				"label": "Sango"
			});
			langs.push(
			{
				"value": "Sanskrit",
				"label": "Sanskrit"
			});
			langs.push(
			{
				"value": "Santali",
				"label": "Santali"
			});
			langs.push(
			{
				"value": "Sardinian",
				"label": "Sardinian"
			});
			langs.push(
			{
				"value": "Sasak",
				"label": "Sasak"
			});
			langs.push(
			{
				"value": "Scots",
				"label": "Scots"
			});
			langs.push(
			{
				"value": "Selkup",
				"label": "Selkup"
			});
			langs.push(
			{
				"value": "Semitic (Other)",
				"label": "Semitic"
			});
			langs.push(
			{
				"value": "Serbian",
				"label": "Serbian"
			});
			langs.push(
			{
				"value": "Serer",
				"label": "Serer"
			});
			langs.push(
			{
				"value": "Shan",
				"label": "Shan"
			});
			langs.push(
			{
				"value": "Shona",
				"label": "Shona"
			});
			langs.push(
			{
				"value": "Sichuan Yi",
				"label": "Sichuan Yi"
			});
			langs.push(
			{
				"value": "Sicilian",
				"label": "Sicilian"
			});
			langs.push(
			{
				"value": "Sidamo",
				"label": "Sidamo"
			});
			langs.push(
			{
				"value": "Sign Languages",
				"label": "Sign Languages"
			});
			langs.push(
			{
				"value": "Siksika",
				"label": "Siksika"
			});
			langs.push(
			{
				"value": "Sindhi",
				"label": "Sindhi"
			});
			langs.push(
			{
				"value": "Sinhalese",
				"label": "Sinhalese"
			});
			langs.push(
			{
				"value": "Sino-Tibetan (Other)",
				"label": "Sino-Tibetan"
			});
			langs.push(
			{
				"value": "Siouan Languages",
				"label": "Siouan Languages"
			});
			langs.push(
			{
				"value": "Skolt Sami",
				"label": "Skolt Sami"
			});
			langs.push(
			{
				"value": "Slave (Athapascan)",
				"label": "Slave (Athapascan)"
			});
			langs.push(
			{
				"value": "Slavic (Other)",
				"label": "Slavic"
			});
			langs.push(
			{
				"value": "Slovak",
				"label": "Slovak"
			});
			langs.push(
			{
				"value": "Slovenian",
				"label": "Slovenian"
			});
			langs.push(
			{
				"value": "Sogdian",
				"label": "Sogdian"
			});
			langs.push(
			{
				"value": "Somali",
				"label": "Somali"
			});
			langs.push(
			{
				"value": "Songhai",
				"label": "Songhai"
			});
			langs.push(
			{
				"value": "Soninke",
				"label": "Soninke"
			});
			langs.push(
			{
				"value": "Sorbian Languages",
				"label": "Sorbian Languages"
			});
			langs.push(
			{
				"value": "Sotho, Southern",
				"label": "Sotho, Southern"
			});
			langs.push(
			{
				"value": "South American Indian (Other)",
				"label": "South American Indian"
			});
			langs.push(
			{
				"value": "South Ndebele",
				"label": "South Ndebele"
			});
			langs.push(
			{
				"value": "Southern Altai",
				"label": "Southern Altai"
			});
			langs.push(
			{
				"value": "Southern Sami",
				"label": "Southern Sami"
			});
			langs.push(
			{
				"value": "Spanish; Castilian",
				"label": "Spanish; Castilian"
			});
			langs.push(
			{
				"value": "Sranan Togo",
				"label": "Sranan Togo"
			});
			langs.push(
			{
				"value": "Sukuma",
				"label": "Sukuma"
			});
			langs.push(
			{
				"value": "Sumerian",
				"label": "Sumerian"
			});
			langs.push(
			{
				"value": "Sundanese",
				"label": "Sundanese"
			});
			langs.push(
			{
				"value": "Susu",
				"label": "Susu"
			});
			langs.push(
			{
				"value": "Swahili",
				"label": "Swahili"
			});
			langs.push(
			{
				"value": "Swati",
				"label": "Swati"
			});
			langs.push(
			{
				"value": "Swedish",
				"label": "Swedish"
			});
			langs.push(
			{
				"value": "Swiss German",
				"label": "Swiss German"
			});
			langs.push(
			{
				"value": "Syriac",
				"label": "Syriac"
			});
			langs.push(
			{
				"value": "Tagalog",
				"label": "Tagalog"
			});
			langs.push(
			{
				"value": "Tahitian",
				"label": "Tahitian"
			});
			langs.push(
			{
				"value": "Tai (Other)",
				"label": "Tai"
			});
			langs.push(
			{
				"value": "Tajik",
				"label": "Tajik"
			});
			langs.push(
			{
				"value": "Tamashek",
				"label": "Tamashek"
			});
			langs.push(
			{
				"value": "Tamil",
				"label": "Tamil"
			});
			langs.push(
			{
				"value": "Tatar",
				"label": "Tatar"
			});
			langs.push(
			{
				"value": "Telugu",
				"label": "Telugu"
			});
			langs.push(
			{
				"value": "Tereno",
				"label": "Tereno"
			});
			langs.push(
			{
				"value": "Tetum",
				"label": "Tetum"
			});
			langs.push(
			{
				"value": "Thai",
				"label": "Thai"
			});
			langs.push(
			{
				"value": "Tibetan",
				"label": "Tibetan"
			});
			langs.push(
			{
				"value": "Tigre",
				"label": "Tigre"
			});
			langs.push(
			{
				"value": "Tigrinya",
				"label": "Tigrinya"
			});
			langs.push(
			{
				"value": "Timne",
				"label": "Timne"
			});
			langs.push(
			{
				"value": "Tiv",
				"label": "Tiv"
			});
			langs.push(
			{
				"value": "Tlingit",
				"label": "Tlingit"
			});
			langs.push(
			{
				"value": "Tok Pisin",
				"label": "Tok Pisin"
			});
			langs.push(
			{
				"value": "Tokelau",
				"label": "Tokelau"
			});
			langs.push(
			{
				"value": "Tonga (Nyasa)",
				"label": "Tonga (Nyasa)"
			});
			langs.push(
			{
				"value": "Tonga (Tonga Islands)",
				"label": "Tonga (Tonga Islands)"
			});
			langs.push(
			{
				"value": "Tsimshian",
				"label": "Tsimshian"
			});
			langs.push(
			{
				"value": "Tsonga",
				"label": "Tsonga"
			});
			langs.push(
			{
				"value": "Tswana",
				"label": "Tswana"
			});
			langs.push(
			{
				"value": "Tumbuka",
				"label": "Tumbuka"
			});
			langs.push(
			{
				"value": "Tupi Languages",
				"label": "Tupi Languages"
			});
			langs.push(
			{
				"value": "Turkish",
				"label": "Turkish"
			});
			langs.push(
			{
				"value": "Turkmen",
				"label": "Turkmen"
			});
			langs.push(
			{
				"value": "Tuvalu",
				"label": "Tuvalu"
			});
			langs.push(
			{
				"value": "Tuvinian",
				"label": "Tuvinian"
			});
			langs.push(
			{
				"value": "Twi",
				"label": "Twi"
			});
			langs.push(
			{
				"value": "Udmurt",
				"label": "Udmurt"
			});
			langs.push(
			{
				"value": "Ugaritic",
				"label": "Ugaritic"
			});
			langs.push(
			{
				"value": "Ukrainian",
				"label": "Ukrainian"
			});
			langs.push(
			{
				"value": "Umbundu",
				"label": "Umbundu"
			});
			langs.push(
			{
				"value": "Undetermined",
				"label": "Undetermined"
			});
			langs.push(
			{
				"value": "Upper Sorbian",
				"label": "Upper Sorbian"
			});
			langs.push(
			{
				"value": "Urdu",
				"label": "Urdu"
			});
			langs.push(
			{
				"value": "Uyghur",
				"label": "Uyghur"
			});
			langs.push(
			{
				"value": "Uzbek",
				"label": "Uzbek"
			});
			langs.push(
			{
				"value": "Vai",
				"label": "Vai"
			});
			langs.push(
			{
				"value": "Venda",
				"label": "Venda"
			});
			langs.push(
			{
				"value": "Vietnamese",
				"label": "Vietnamese"
			});
			langs.push(
			{
				"value": "Volapuk",
				"label": "Volapuk"
			});
			langs.push(
			{
				"value": "Votic",
				"label": "Votic"
			});
			langs.push(
			{
				"value": "Wakashan Languages",
				"label": "Wakashan Languages"
			});
			langs.push(
			{
				"value": "Walamo",
				"label": "Walamo"
			});
			langs.push(
			{
				"value": "Walloon",
				"label": "Walloon"
			});
			langs.push(
			{
				"value": "Waray",
				"label": "Waray"
			});
			langs.push(
			{
				"value": "Washo",
				"label": "Washo"
			});
			langs.push(
			{
				"value": "Welsh",
				"label": "Welsh"
			});
			langs.push(
			{
				"value": "Western Frisian",
				"label": "Western Frisian"
			});
			langs.push(
			{
				"value": "Wolof",
				"label": "Wolof"
			});
			langs.push(
			{
				"value": "Xhosa",
				"label": "Xhosa"
			});
			langs.push(
			{
				"value": "Yakut",
				"label": "Yakut"
			});
			langs.push(
			{
				"value": "Yao",
				"label": "Yao"
			});
			langs.push(
			{
				"value": "Yapese",
				"label": "Yapese"
			});
			langs.push(
			{
				"value": "Yiddish",
				"label": "Yiddish"
			});
			langs.push(
			{
				"value": "Yoruba",
				"label": "Yoruba"
			});
			langs.push(
			{
				"value": "Yupik Languages",
				"label": "Yupik Languages"
			});
			langs.push(
			{
				"value": "Zande",
				"label": "Zande"
			});
			langs.push(
			{
				"value": "Zapotec",
				"label": "Zapotec"
			});
			langs.push(
			{
				"value": "Zazaki",
				"label": "Zazaki"
			});
			langs.push(
			{
				"value": "Zenaga",
				"label": "Zenaga"
			});
			langs.push(
			{
				"value": "Zhuang",
				"label": "Zhuang"
			});
			langs.push(
			{
				"value": "Zulu",
				"label": "Zulu"
			});
			langs.push(
			{
				"value": "Zuni",
				"label": "Zuni"
			});
			return langs;
		};

		service.getRosterTerminationReasons = function getRosterTerminationReasons()
		{
			var reasons = [];
			reasons.push(
			{
				"value": "39",
				"label": "Assigned member status ended; roster transferred per physician request"
			});
			reasons.push(
			{
				"value": "59",
				"label": "Enrolment ended; patient out of geographic area"
			});
			reasons.push(
			{
				"value": "57",
				"label": "Enrolment terminated by patient"
			});
			reasons.push(
			{
				"value": "12",
				"label": "Health Number error"
			});
			reasons.push(
			{
				"value": "38",
				"label": "Long Term Care enrolment ended; patient has left Long Term Care"
			});
			reasons.push(
			{
				"value": "82",
				"label": "Ministry has not received enrolment/ Consent form"
			});
			reasons.push(
			{
				"value": "60",
				"label": "No current eligibility"
			});
			reasons.push(
			{
				"value": "73",
				"label": "No current eligibility"
			});
			reasons.push(
			{
				"value": "74",
				"label": "No current eligibility"
			});
			reasons.push(
			{
				"value": "37",
				"label": "Original enrolment ended; patient now enrolled as Long Term Care"
			});
			reasons.push(
			{
				"value": "36",
				"label": "Original enrolment ended; patient now re-enroled"
			});
			reasons.push(
			{
				"value": "24",
				"label": "Patient added to roster in error"
			});
			reasons.push(
			{
				"value": "14",
				"label": "Patient identified as deceased on ministry database"
			});
			reasons.push(
			{
				"value": "51",
				"label": "Patient no longer meets selection criteria for your roster"
			});
			reasons.push(
			{
				"value": "41",
				"label": "Patient no longer meets selection criteria for your roster - assigned to another physician"
			});
			reasons.push(
			{
				"value": "61",
				"label": "Patient out of geographic area; address over-ride applied"
			});
			reasons.push(
			{
				"value": "62",
				"label": "Patient out of geographic area; address over-ride removed"
			});
			reasons.push(
			{
				"value": "35",
				"label": "Patient transferred from roster per physician request"
			});
			reasons.push(
			{
				"value": "42",
				"label": "Physician ended enrolment; patient entered Long Term Care facility"
			});
			reasons.push(
			{
				"value": "54",
				"label": "Physician ended enrolment; patient left province"
			});
			reasons.push(
			{
				"value": "53",
				"label": "Physician ended enrolment; patient moved out of geographic area"
			});
			reasons.push(
			{
				"value": "56",
				"label": "Physician ended enrolment; per patient request"
			});
			reasons.push(
			{
				"value": "44",
				"label": "Physician ended patient enrolment"
			});
			reasons.push(
			{
				"value": "40",
				"label": "Physician reported member as deceased"
			});
			reasons.push(
			{
				"value": "32",
				"label": "Pre-member/ Assigned member ended; now enrolled or registered with photo health card"
			});
			reasons.push(
			{
				"value": "30",
				"label": "Pre-member/ Assigned member ended; now enrolled or registered with red and white health card"
			});
			reasons.push(
			{
				"value": "33",
				"label": "Termination reason cannot be released (due to patient confidentiality)"
			});
			reasons.push(
			{
				"value": "84",
				"label": "Termination reason cannot be released (due to patient confidentiality)"
			});
			reasons.push(
			{
				"value": "90",
				"label": "Termination reason cannot be released (due to patient confidentiality)"
			});
			reasons.push(
			{
				"value": "91",
				"label": "Termination reason cannot be released (due to patient confidentiality)"
			});
			return reasons;
		};

		service.getSecurityQuestions = function getSecurityQuestions()
		{
			var questions = [];
			questions.push(
			{
				"value": "What was the name of your high school?",
				"label": "What was the name of your high school?"
			});
			questions.push(
			{
				"value": "What is your spouse's maiden name?",
				"label": "What is your spouse's maiden name?"
			});
			questions.push(
			{
				"value": "What is the name of the street you grew up on?",
				"label": "What is the name of the street you grew up on?"
			});
			questions.push(
			{
				"value": "In what city were you born?",
				"label": "In what city were you born?"
			});
			questions.push(
			{
				"value": "What is the middle name of your oldest child?",
				"label": "What is the middle name of your oldest child?"
			});
			questions.push(
			{
				"value": "What is your oldest cousin's first name?",
				"label": "What is your oldest cousin's first name?"
			});
			questions.push(
			{
				"value": "What is your mother's middle name?",
				"label": "What is your mother's middle name?"
			});
			questions.push(
			{
				"value": "What is your grandmother's first name?",
				"label": "What is your grandmother's first name?"
			});
			questions.push(
			{
				"value": "What year did you graduate from high school?",
				"label": "What year did you graduate from high school?"
			});
			return questions;
		};

		service.getConsultUrgencies = function getConsultUrgencies()
		{
			var urgencies = [];
			urgencies.push(
			{
				value: "2",
				name: "Non-Urgent"
			});
			urgencies.push(
			{
				value: "1",
				name: "Urgent"
			});
			urgencies.push(
			{
				value: "3",
				name: "Return"
			});
			return urgencies;
		};

		service.getConsultRequestStatuses = function getConsultRequestStatuses()
		{
			var statuses = [];
			statuses.push(
			{
				value: "1",
				name: "Not Complete"
			});
			statuses.push(
			{
				value: "2",
				name: "Preliminary Pending Specialist"
			});
			statuses.push(
			{
				value: "3",
				name: "Pending Callback"
			});
			statuses.push(
			{
				value: "4",
				name: "Completed"
			});
			statuses.push(
			{
				value: "5",
				name: "Cancelled"
			});
			statuses.push(
			{
				value: "6",
				name: "Appointment Booked"
			});
			statuses.push(
			{
				value: "7",
				name: "Deleted"
			});
			return statuses;
		};

		service.getConsultResponseStatuses = function getConsultResponseStatuses()
		{
			var statuses = [];
			statuses.push(
			{
				value: 1,
				name: "Not Complete"
			});
			statuses.push(
			{
				value: 2,
				name: "Pending Referring Doctor Callback"
			});
			statuses.push(
			{
				value: 3,
				name: "Pending Patient Callback"
			});
			statuses.push(
			{
				value: 4,
				name: "Completed"
			});
			statuses.push(
			{
				value: 5,
				name: "Cancelled"
			});
			return statuses;
		};

		service.getHours = function getHours()
		{
			return ["00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
				"13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"
			];
		};

		service.getMinutes = function getMinutes()
		{
			return ["00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
				"11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
				"21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
				"31", "32", "33", "34", "35", "36", "37", "38", "39", "40",
				"41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
				"51", "52", "53", "54", "55", "56", "57", "58", "59"
			];
		};

		service.getRxInteractionLevels = function getRxInteractionLevels()
		{
			var levels = [];
			levels.push(
			{
				value: 0,
				name: "Not Specified"
			});
			levels.push(
			{
				value: 1,
				name: "Low"
			});
			levels.push(
			{
				value: 2,
				name: "Medium"
			});
			levels.push(
			{
				value: 3,
				name: "High"
			});
			levels.push(
			{
				value: 4,
				name: "None"
			});
			return levels;
		};

		return service;
	}
]);