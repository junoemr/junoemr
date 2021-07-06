import { PartialDate } from "../../../generated";
import PartialDateModel from "../models/partialDateModel"

export abstract class PartialDateConverter
{
    public static convertToPartialDateModel(from: PartialDate): PartialDateModel
    {
        if (!from)
        {
            return null;
        }

        const partialDate = from as any;
        const partialDateModel = new PartialDateModel(null, null, null);

        if (partialDate.year)
        {
            partialDateModel.year = parseInt(partialDate.year);
        }

        if (partialDate.month)
        {
            let monthValue = 1;
            switch (partialDate.month)
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

        if (partialDate.day)
        {
            partialDateModel.day = parseInt(partialDate.day);
        }

        return partialDateModel;
    }

    public static convertToPartialDate(from: PartialDateModel): PartialDate
    {
        if (!from)
        {
            return null;
        }

        const partialDateModel = from as any;
        const partialDate = {
            "year": {leap: false, value: 1},
            "month": 1,
            "day": 1
        } as PartialDate;

        partialDate.year.leap = this.isLeapYear(partialDateModel.year);
        partialDate.year.value = parseInt(partialDateModel.year);

        partialDate.month = null;
        if (partialDateModel.month)
        {
            partialDate.month = partialDateModel.month;
        }

        partialDate.day = null;
        if (partialDateModel.day)
        {
            partialDate.day = partialDateModel.day;
        }

        return partialDate;
    }

    public static isLeapYear(year)
    {
        return (year % 4 == 0 &&
            year % 100 == 0 &&
            year % 400 == 0);
    }
}