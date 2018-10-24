/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */
package org.oscarehr.demographic.util;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class HinValidator
{
	// map of character position index to weight values
	private static final HashMap<Integer, Integer> positionMap;

	static {
		positionMap = new HashMap<>(10);
		positionMap.put(0, 0);
		positionMap.put(1, 2);
		positionMap.put(2, 4);
		positionMap.put(3, 8);
		positionMap.put(4, 5);
		positionMap.put(5, 10);
		positionMap.put(6, 9);
		positionMap.put(7, 7);
		positionMap.put(8, 3);
		positionMap.put(9, 0);
	}

	/**
	 * This method will return false if the hin is clearly invalid. It will return true otherwise. This means that the default is true and anything it can't figure out will return true. As an example if the hinType is null then there's no validation
	 * algorithm so it will return true.
	 */
	public static boolean isValid(String hin, String provinceCode)
	{
		if(hin == null || hin.trim().isEmpty() || provinceCode == null || provinceCode.length() != 2)
		{
			return false;
		}
		switch(provinceCode.toUpperCase())
		{
			case "BC":
				return isValidBritishColumbia(hin);
			case "ON":
				return isValidOntario(hin);
			case "QC":
				return (hin.length() == 12);
			default:
				return true;
		}
	}

	private static boolean isValidOntario(String hin)
	{
		return (StringUtils.isNumeric(hin) && hin.length() == 10 && validMod10ChecksumON(hin));
	}

	private static boolean isValidBritishColumbia(String hin)
	{
		if(!StringUtils.isNumeric(hin) || hin.length() > 10)
		{
			return false;
		}
		/* A PHN is 10 digits, starting with a 9 */
		if(hin.length() == 10 && hin.startsWith("9"))
		{
			return validMod11ChecksumBC(hin);
		}
		/* Allow 8 digit numbers because OGW has some correctional ids that are
		 * 8 digits long but don't follow the mod10 scheme */
		else if(hin.length() == 8)
		{
			return true;
		}
		else
		{
			return validMod10ChecksumBC(hin);
		}
	}

	private static boolean validMod10ChecksumON(String hin)
	{
		int sum = 0;
		char[] base_number = new char[9];
		int check_digit = digitAt(hin, 9);
		System.arraycopy(hin.toCharArray(), 0, base_number, 0, 9);

		for(int i=0; i< base_number.length; i++)
		{
			int digit = Character.getNumericValue(base_number[i]);
			if(i%2 == 0)
			{
				int doubled = digit * 2;
				digit = (doubled / 10) + (doubled % 10);
			}
			sum += digit;
		}
		return (check_digit == ((10 - (sum % 10)) % 10));
	}

	private static double doubleAndAdd(int digit)
	{
		double sum = (digit * 2);
		return ((sum / 10) + (sum % 10));
	}

	private static boolean validMod10ChecksumBC(String hin)
	{
		// Pad with 0's if it's shorter
		hin = StringUtils.leftPad(hin, 9, "0");

		// 1. Discard least significant digit (last number) from number to be verified.
		int check_digit = digitAt(hin, 8);

		// 2. Sum A = Sum of even-position digits working left from the right-most digit.
		int sum_a = digitAt(hin, 6) + digitAt(hin, 4) + digitAt(hin, 2) + digitAt(hin, 0);

		// 3. Sum B = Sum of odd-position digits after multiplying by 2.
		double sum_b = (doubleAndAdd(digitAt(hin, 7)) + doubleAndAdd(digitAt(hin, 5)) +
				doubleAndAdd(digitAt(hin, 3)) + doubleAndAdd(digitAt(hin, 1)));

		// 4. Sum C = Sum A + Sum B.
		int sum_c = (int) (sum_a + sum_b);

		// 5. Check-digit = 10 - least significant digit of Sum C.
		int calculated_check_digit = (10 - (sum_c % 10));

		// 6. Digit discarded from number in Step 1 should equal the calculated check-digit.
		return (calculated_check_digit == check_digit);
	}

	private static boolean validMod11ChecksumBC(String hin)
	{
		int check_digit = digitAt(hin, 9);
		int sum = 0;

		for(Map.Entry<Integer, Integer> entry : positionMap.entrySet())
		{
			sum += digitAt(hin, entry.getKey()) * entry.getValue();
		}

		int calculated_check_digit = 11 - (sum -((int)Math.floor(sum / 11)) * 11);
		return (calculated_check_digit == check_digit);
	}

	private static int digitAt(String str, int index)
	{
		return Character.getNumericValue(str.charAt(index));
	}
}
