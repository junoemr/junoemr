import { PartialDate } from "../../../generated";
import PartialDateModel from "../models/partialDateModel"
import AbstractConverter from "../../lib/conversion/AbstractConverter";

export default class PartialDateConverter extends AbstractConverter<PartialDate, PartialDateModel>
{
    convert(from: PartialDate): PartialDateModel
    {
        if (!from || !from.year)
        {
            return null;
        }

        const partialDateModel = new PartialDateModel(null, null, null);
        partialDateModel.year = from.year.toString();

        if (from.month)
        {
            let monthValue = "01";
            switch (from.month)
            {
                case PartialDate.MonthEnum.JANUARY:
                    break;
                case PartialDate.MonthEnum.FEBRUARY:
                    monthValue = "02";
                    break;
                case PartialDate.MonthEnum.MARCH:
                    monthValue = "03";
                    break;
                case PartialDate.MonthEnum.APRIL:
                    monthValue = "04";
                    break;
                case PartialDate.MonthEnum.MAY:
                    monthValue = "05";
                    break;
                case PartialDate.MonthEnum.JUNE:
                    monthValue = "06";
                    break;
                case PartialDate.MonthEnum.JULY:
                    monthValue = "07";
                    break;
                case PartialDate.MonthEnum.AUGUST:
                    monthValue = "08";
                    break;
                case PartialDate.MonthEnum.SEPTEMBER:
                    monthValue = "09";
                    break;
                case PartialDate.MonthEnum.OCTOBER:
                    monthValue = "10";
                    break;
                case PartialDate.MonthEnum.NOVEMBER:
                    monthValue = "11";
                    break;
                case PartialDate.MonthEnum.DECEMBER:
                    monthValue = "12";
                    break;
            }
            partialDateModel.month = monthValue;
        }

        if (from.day)
        {
            partialDateModel.day = from.day.toString();
        }

        return partialDateModel;
    }
}