import AbstractConverter from "../../conversion/AbstractConverter";
import {FormTo1} from "../../../../generated";
import EFormInstance from "../model/EFormInstance";
import {FormType} from "../model/FormType";
import ArgumentError from "../../error/ArgumentError";
import moment from "moment";

export default class FormTo1ToEFormInstanceConverter extends AbstractConverter<FormTo1, EFormInstance>
{

	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: FormTo1): EFormInstance
	{
		if (!from)
		{
			return null;
		}

		if (from.type === FormType.Form)
		{
			throw new ArgumentError("FormTo1ToEFormInstanceConverter only converts FormTo1 of type FormType.EForm ('eform') type provided: " + from.type)
		}

		return new EFormInstance(from.formId?.toString(), from.id?.toString(), from.name, from.status, from.subject, from.demographicNo?.toString(), moment(from.date));
	}

}