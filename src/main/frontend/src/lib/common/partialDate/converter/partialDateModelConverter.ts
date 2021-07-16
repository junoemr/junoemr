import PartialDateModel from "../model/partialDateModel";
import {PartialDate} from "../../../../../generated";
import AbstractConverter from "../../../conversion/AbstractConverter";

export default class PartialDateModelConverter extends AbstractConverter<PartialDateModel, PartialDate>
{
    convert(from: PartialDateModel): PartialDate
    {
        if (!from || !from.year )
        {
            return null;
        }

       const partialDate = {
            "year":  1900,
            "month": 1,
            "day": 1
        };

        partialDate.year = parseInt(from.year.toString()) || null;
        if (typeof (partialDate.year) !== "number")
        {
            return null;
        }

        partialDate.month = null;
        if (from.month)
        {
            switch(from.month)
            {
                case 1:
                    partialDate.month = PartialDate.MonthEnum.JANUARY;
                    break;
                case 2:
                    partialDate.month = PartialDate.MonthEnum.FEBRUARY;
                    break;
                case 3:
                    partialDate.month = PartialDate.MonthEnum.MARCH;
                    break;
                case 4:
                    partialDate.month = PartialDate.MonthEnum.APRIL;
                    break;
                case 5:
                    partialDate.month = PartialDate.MonthEnum.MAY;
                    break;
                case 6:
                    partialDate.month = PartialDate.MonthEnum.JUNE;
                    break;
                case 7:
                    partialDate.month = PartialDate.MonthEnum.JULY;
                    break;
                case 8:
                    partialDate.month = PartialDate.MonthEnum.AUGUST;
                    break;
                case 9:
                    partialDate.month = PartialDate.MonthEnum.SEPTEMBER;
                    break;
                case 10:
                    partialDate.month = PartialDate.MonthEnum.OCTOBER;
                    break;
                case 11:
                    partialDate.month = PartialDate.MonthEnum.NOVEMBER;
                    break;
                case 12:
                    partialDate.month = PartialDate.MonthEnum.DECEMBER;
                    break;
            }
        }

        partialDate.day = null;
        if (from.day)
        {
            partialDate.day = parseInt(from.day.toString()) || null;
        }

        // This is needed so that it compiles. The generated class is not being generated correctly (PartialDate.ts)
        // @ts-ignore
        return partialDate;
    }
}