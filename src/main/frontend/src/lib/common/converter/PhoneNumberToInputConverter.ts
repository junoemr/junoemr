import AbstractConverter from "../../conversion/AbstractConverter";
import {PhoneNumberModel} from "../../../../generated";
import PhoneNumber from "../model/PhoneNumber";


export default class PhoneNumberToInputConverter extends AbstractConverter<PhoneNumber, PhoneNumberModel>
{
	convert(from: PhoneNumber): PhoneNumberModel
	{
		if(!from)
		{
			return null;
		}

		let input = {} as PhoneNumberModel;

		input.number = from.number;
		input.extension = from.extension;
		input.phoneType = from.phoneType;
		input.primaryContactNumber = from.primaryContactNumber;

		return input;
	}
}