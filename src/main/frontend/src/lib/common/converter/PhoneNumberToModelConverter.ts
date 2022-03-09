import AbstractConverter from "../../conversion/AbstractConverter";
import {PhoneNumberModel} from "../../../../generated";
import PhoneNumber from "../model/PhoneNumber";


export default class PhoneNumberToModelConverter extends AbstractConverter<PhoneNumberModel, PhoneNumber>
{
	convert(from: PhoneNumberModel): PhoneNumber
	{
		if(!from)
		{
			return null;
		}
		return new PhoneNumber(from.number, from.extension, from.phoneType, from.primaryContactNumber);
	}
}