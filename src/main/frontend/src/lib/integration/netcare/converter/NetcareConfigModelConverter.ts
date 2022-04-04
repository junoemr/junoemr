import AbstractConverter from "../../../conversion/AbstractConverter";
import {NetcareConfigModel} from "../../../../../generated";
import NetcareConfig from "../model/NetcareConfig";

export default class NetcareConfigModelConverter extends AbstractConverter<NetcareConfigModel, NetcareConfig>
{
	convert(from: NetcareConfigModel): NetcareConfig
	{
		let model = new NetcareConfig();

		model.conformanceCode = from.conformanceCode;
		model.launcherUrl = from.launcherUrl;
		model.loginUrl = from.loginUrl;
		model.logoutUrl = from.logoutUrl;

		return model;
	}
}