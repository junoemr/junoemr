import { PartialDate } from "../../../../../generated";
import PartialDateModel from "../model/partialDateModel"
import AbstractConverter from "../../../conversion/AbstractConverter";

export default class PartialDateConverter extends AbstractConverter<PartialDate, PartialDateModel>
{
    convert(from: PartialDate): PartialDateModel
    {
        if (!from || !from.year)
        {
            return null;
        }

        const partialDateModel = new PartialDateModel(null, null, null);
        partialDateModel.year = parseInt(from.year.toString());

        if (from.month)
        {
            let monthValue = 1;
            switch (from.month)
            {
                case PartialDate.MonthEnum.January:
                    break;
                case PartialDate.MonthEnum.February:
                    monthValue = 2;
                    break;
                case PartialDate.MonthEnum.March:
                    monthValue = 3;
                    break;
                case PartialDate.MonthEnum.April:
                    monthValue = 4;
                    break;
                case PartialDate.MonthEnum.May:
                    monthValue = 5;
                    break;
                case PartialDate.MonthEnum.June:
                    monthValue = 6;
                    break;
                case PartialDate.MonthEnum.July:
                    monthValue = 7;
                    break;
                case PartialDate.MonthEnum.August:
                    monthValue = 8;
                    break;
                case PartialDate.MonthEnum.September:
                    monthValue = 9;
                    break;
                case PartialDate.MonthEnum.October:
                    monthValue = 10;
                    break;
                case PartialDate.MonthEnum.November:
                    monthValue = 11;
                    break;
                case PartialDate.MonthEnum.December:
                    monthValue = 12;
                    break;
            }
            partialDateModel.month = monthValue;
        }

        if (from.day)
        {
            partialDateModel.day = from.day;
        }

        return partialDateModel;
    }
}