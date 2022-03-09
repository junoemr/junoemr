import AbstractConverter from "../../conversion/AbstractConverter";
import {ProviderModel} from "../../../../generated";
import SimpleProvider from "../model/SimpleProvider";

export default class SimpleProviderToInputConverter extends AbstractConverter<SimpleProvider, ProviderModel>
{
	convert(model: SimpleProvider): ProviderModel
	{
		if(!model)
		{
			return null;
		}

		let transfer = {} as ProviderModel;
		transfer.id = model.id;
		transfer.firstName = model.firstName;
		transfer.lastName = model.lastName;
		transfer.ohipNumber = model.ohipNumber;

		return transfer;
	}
}