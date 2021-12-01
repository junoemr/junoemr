import {
	HrmCategoryModel,
	HRMCategoryTransferInbound
} from "../../../../../generated";
import HrmSubClass from "../model/HrmSubClass";

public static fromTransferArray(transferList: HrmCategoryModel[]): HrmCategory[]
{
	if (!transferList)
	{
		return null;
	}

	return transferList.map(HrmCategory.fromTransfer);
}

public static fromTransfer(transfer: HrmCategoryModel): HrmCategory
{
	if (!transfer)
	{
		return null;
	}

	const category = new HrmCategory();
	category._id = transfer.id;
	category._name = transfer.name;
	category._subClasses = HrmSubClass.fromTransferList(transfer.subClasses);

	return category;
}

public static toTransfer(category: HrmCategory): HRMCategoryTransferInbound
{
	console.log("in transfer");
	console.log({
		name: category.name,
		subClasses: HrmSubClass.toTransferList(category.subClasses),
	});
	return {
		name: category.name,
		subClasses: HrmSubClass.toTransferList(category.subClasses),
	}
}