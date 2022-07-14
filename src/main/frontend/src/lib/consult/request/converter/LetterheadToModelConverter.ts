import AbstractConverter from "../../../conversion/AbstractConverter";
import {LetterheadTo1} from "../../../../../generated";
import Letterhead from "../model/Letterhead";
import PhoneNumber from "../../../common/model/PhoneNumber";
import {PhoneType} from "../../../common/model/PhoneType";
import Address from "../../../common/model/Address";
import {AddressResidencyStatus} from "../../../common/model/AddressResidencyStatus";

export default class LetterheadToModelConverter extends AbstractConverter<LetterheadTo1, Letterhead>
{
	convert(from: LetterheadTo1): Letterhead
	{
		if(!from)
		{
			return null;
		}

		let model = new Letterhead(from.id);
		model.name = from.name;

		if(from.address)
		{
			let address = new Address(AddressResidencyStatus.Current);
			address.addressLine1 = from.address;
			model.address = address;
		}
		if(from.phone)
		{
			model.phone = new PhoneNumber(from.phone, null, PhoneType.Work, true);
		}
		if(from.fax)
		{
			model.fax = new PhoneNumber(from.fax, null, PhoneType.Fax, false);
		}

		return model;
	}
}