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
                    partialDate.month = PartialDate.MonthEnum.January;
                    break;
                case 2:
                    partialDate.month = PartialDate.MonthEnum.February;
                    break;
                case 3:
                    partialDate.month = PartialDate.MonthEnum.March;
                    break;
                case 4:
                    partialDate.month = PartialDate.MonthEnum.April;
                    break;
                case 5:
                    partialDate.month = PartialDate.MonthEnum.May;
                    break;
                case 6:
                    partialDate.month = PartialDate.MonthEnum.June;
                    break;
                case 7:
                    partialDate.month = PartialDate.MonthEnum.July;
                    break;
                case 8:
                    partialDate.month = PartialDate.MonthEnum.August;
                    break;
                case 9:
                    partialDate.month = PartialDate.MonthEnum.September;
                    break;
                case 10:
                    partialDate.month = PartialDate.MonthEnum.October;
                    break;
                case 11:
                    partialDate.month = PartialDate.MonthEnum.November;
                    break;
                case 12:
                    partialDate.month = PartialDate.MonthEnum.December;
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