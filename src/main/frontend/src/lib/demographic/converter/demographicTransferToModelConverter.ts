import AbstractConverter from "../../conversion/AbstractConverter";
import Demographic from "../model/demographic";
import {DemographicModel} from "../../../../generated";
import moment from "moment";

export default class DemographicTransferToModelConverter extends AbstractConverter<DemographicModel, Demographic>
{
	convert(from: DemographicModel, args: any): Demographic
	{
		let model = new Demographic();
		Object.assign(model, from);

		model.dateOfBirth = moment(from.dateOfBirth);
		model.healthNumberEffectiveDate = moment(from.healthNumberEffectiveDate);
		model.healthNumberRenewDate = moment(from.healthNumberRenewDate);
		model.patientStatusDate = moment(from.patientStatusDate);
		model.dateJoined = moment(from.dateJoined);
		model.dateEnded = moment(from.dateEnded);
		model.lastUpdateDateTime = moment(from.lastUpdateDateTime);

		return model;
	}
}