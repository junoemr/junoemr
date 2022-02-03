import AbstractConverter from "../../conversion/AbstractConverter";
import {AddressModel} from "../../../../generated";
import Address from "../model/Address";

export default class AddressToInputConverter extends AbstractConverter<Address, AddressModel>
{
	convert(from: Address, args: any): AddressModel
	{
		let input = {} as AddressModel;

		input.addressLine1 = from.addressLine1;
		input.addressLine2 = from.addressLine2;
		input.city = from.city;
		input.regionCode = from.regionCode;
		input.countryCode = from.countryCode;
		input.postalCode = from.postalCode;
		input.residencyStatus = from.residencyStatus;

		return input;
	}
}