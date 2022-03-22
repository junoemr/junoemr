import FaxAccount from "../model/FaxAccount";
import FaxAccountProvider from "./FaxAccountProvider";
import {FaxAccountType} from "../model/FaxAccountType";
import RingCentralAccountProvider from "./RingCentralAccountProvider";
import SRFaxAccountProvider from "./SRFaxAccountProvider";

export default class FaxAccountProviderFactory
{
	public static creatAccountProvider(faxAccount: FaxAccount): FaxAccountProvider
	{
		switch (faxAccount.accountType)
		{
			case FaxAccountType.Ringcentral: return new RingCentralAccountProvider(faxAccount);
			case FaxAccountType.Srfax: return new SRFaxAccountProvider(faxAccount);
			default: return null;
		}
	}
}