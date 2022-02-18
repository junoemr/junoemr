import AbstractConverter from "../../conversion/AbstractConverter";
import {AddressModel} from "../../../../generated";
import Address from "../model/Address";
import {Province} from "../../constants/Province";
import {CountryCode} from "../../constants/CountryCode";
import {USStateCode} from "../../constants/USStateCode";

export default class AddressToModelConverter extends AbstractConverter<AddressModel, Address>
{
	convert(from: AddressModel): Address
	{
		if(!from)
		{
			return null;
		}

		let model = new Address();

		model.addressLine1 = from.addressLine1;
		model.addressLine2 = from.addressLine2;
		model.city = from.city;
		model.countryCode = from.countryCode as any as CountryCode;
		if(model.countryCode === CountryCode.US)
		{
			model.regionCode = from.regionCode as any as USStateCode;
		}
		else
		{
			model.regionCode = from.regionCode as any as Province;
		}
		model.postalCode = from.postalCode;
		model.residencyStatus = from.residencyStatus;

		return model;
	}
}