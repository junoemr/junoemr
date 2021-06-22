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
        const partialDateModel = new PartialDateModel(1940,1,1);

        partialDateModel.setYear("");
        partialDateModel.setMonth("");
        partialDateModel.setDay("");

        if (partialDate.year)
        {
            partialDateModel.setYear(parseInt(partialDate.year));
        }

        if (partialDate.month)
        {
            let monthValue = "01";
            switch (partialDate.month)
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
            partialDateModel.setMonth(monthValue);
        }

        if (partialDate.day)
        {
            partialDateModel.setDay(parseInt(partialDate.day));
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
        };

        partialDate.year.leap = this.isLeapYear(partialDateModel.getYear());
        partialDate.year.value = partialDateModel.getYear();
        partialDate.month = partialDateModel.getMonth();
        partialDate.day = partialDateModel.getDay();

        return partialDate;
    }

    public static isLeapYear(year)
    {
        if (year % 4 == 0 &&
            year % 100 == 0 &&
            year % 400 == 0)
        {
            return true;
        }
        return false;
    }
}