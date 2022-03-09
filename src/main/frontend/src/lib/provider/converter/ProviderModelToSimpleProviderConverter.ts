import AbstractConverter from "../../conversion/AbstractConverter";
import {ProviderModel} from "../../../../generated";
import SimpleProvider from "../model/SimpleProvider";

export default class ProviderModelToSimpleProviderConverter extends AbstractConverter<ProviderModel, SimpleProvider>
{
	convert(from: ProviderModel): SimpleProvider
	{
		if(!from)
		{
			return null;
		}

		let model = new SimpleProvider();
		model.id = from.id;
		model.firstName = from.firstName;
		model.lastName = from.lastName;
		model.ohipNumber = from.ohipNumber;

		return model;
	}
}