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
                case PartialDate.MonthEnum.JANUARY:
                    break;
                case PartialDate.MonthEnum.FEBRUARY:
                    monthValue = 2;
                    break;
                case PartialDate.MonthEnum.MARCH:
                    monthValue = 3;
                    break;
                case PartialDate.MonthEnum.APRIL:
                    monthValue = 4;
                    break;
                case PartialDate.MonthEnum.MAY:
                    monthValue = 5;
                    break;
                case PartialDate.MonthEnum.JUNE:
                    monthValue = 6;
                    break;
                case PartialDate.MonthEnum.JULY:
                    monthValue = 7;
                    break;
                case PartialDate.MonthEnum.AUGUST:
                    monthValue = 8;
                    break;
                case PartialDate.MonthEnum.SEPTEMBER:
                    monthValue = 9;
                    break;
                case PartialDate.MonthEnum.OCTOBER:
                    monthValue = 10;
                    break;
                case PartialDate.MonthEnum.NOVEMBER:
                    monthValue = 11;
                    break;
                case PartialDate.MonthEnum.DECEMBER:
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