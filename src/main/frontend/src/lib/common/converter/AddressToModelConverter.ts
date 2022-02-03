import AbstractConverter from "../../conversion/AbstractConverter";
import {AddressModel} from "../../../../generated";
import Address from "../model/Address";

export default class AddressToModelConverter extends AbstractConverter<AddressModel, Address>
{
	convert(from: AddressModel, args: any): Address
	{
		let model = new Address();

		model.addressLine1 = from.addressLine1;
		model.addressLine2 = from.addressLine2;
		model.city = from.city;
		model.regionCode = from.regionCode;
		model.countryCode = from.countryCode;
		model.postalCode = from.postalCode;
		model.residencyStatus = from.residencyStatus;

		return model;
	}
}