import PartialDateModel from "../models/partialDateModel";
import {PartialDate} from "../../../generated";
import AbstractConverter from "../../lib/conversion/AbstractConverter";

export default class PartialDateModelConverter extends AbstractConverter<PartialDateModel, PartialDate>
{
    convert(from: PartialDateModel): PartialDate
    {
        if (!from || !from.year)
        {
            return null;
        }

        const partialDate = {
            "year": {leap: false, value: 1},
            "month": 1,
            "day": 1
        } as PartialDate;

        partialDate.year.leap = this.isLeapYear(from.year);
        partialDate.year.value = from.year;

        partialDate.month = null;
        if (from.month)
        {
            partialDate.month = parseInt(from.month.toString());
        }

        partialDate.day = null;
        if (from.day)
        {
            partialDate.day = from.day;
        }

        return partialDate;
    }

    private isLeapYear(year)
    {
        return (year % 4 == 0 &&
            year % 100 == 0 &&
            year % 400 == 0);
    }
}