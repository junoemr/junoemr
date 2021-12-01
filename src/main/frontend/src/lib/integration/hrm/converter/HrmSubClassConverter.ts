import {
	HrmSubClassModel,
	HRMSubClassTransferInbound
} from "../../../../../generated";
import HrmSubClass, {HrmReportClass} from "../model/HrmSubClass";

export default class HrmSubClassConverter
{
	public static fromTransfer(transfer: HrmSubClassModel): HrmSubClass
	{
		if (transfer == null)
		{
			return null;
		}

		const hrmSubClass = new HrmSubClass();
		hrmSubClass.id = transfer.id;
		hrmSubClass.hrmCategoryId = transfer.hrmCategoryId;
		hrmSubClass.facilityNumber = transfer.facilityNumber;
		hrmSubClass.reportClass = transfer.className as HrmReportClass;
		hrmSubClass.subClassName = transfer.subClassName;
		hrmSubClass.accompanyingSubClassName = transfer.accompanyingSubClassName;

		return hrmSubClass;
	}

	public static fromTransferList(transferList: HrmSubClassModel[]): HrmSubClass[]
	{
		if (!transferList)
		{
			return null;
		}

		return transferList.map(transfer => { return HrmSubClassConverter.fromTransfer(transfer)})
	}

	public static toTransfer(hrmSubClass: HrmSubClass): HRMSubClassTransferInbound
	{
		return {... hrmSubClass}
	}

  public static toTransferList(subClasses: HrmSubClass[]): HRMSubClassTransferInbound[]
  {
    if (subClasses === null)
    {
      return null;
    }

    return subClasses.map(subClass => HrmSubClassConverter.toTransfer(subClass));
  }
}