import {PartialDate} from "../../../../../generated";
import PartialDateModel from "../model/partialDateModel"
import AbstractConverter from "../../../conversion/AbstractConverter";
import moment from "moment/moment";

export default class PartialDateConverter extends AbstractConverter<PartialDate, PartialDateModel>
{
    convert(from: PartialDate | string): PartialDateModel
    {
        if (!from)
        {
            return null;
        }

        if (typeof from === "string")
        {
            return this.convertString(from);
        }
        else
        {
            return this.convertObject(from);
        }
    }

    private convertString(from: string): PartialDateModel
    {
        let partialDateModel;
        let parts = from.split("T");

        if(parts.length == 2) //datetime
        {
            let asMoment = moment(from);
            partialDateModel = new PartialDateModel(asMoment.year(), asMoment.month(), asMoment.day());
        }
        else
        {
            const dateParts = parts[0].split("-");
            if(dateParts.length === 3)
            {
                partialDateModel = new PartialDateModel(Number(dateParts[0]), Number(dateParts[1]), Number(dateParts[2]));
            }
            else if(dateParts.length === 2)
            {
                partialDateModel = new PartialDateModel(Number(dateParts[0]), Number(dateParts[1]), null);
            }
            else if(dateParts.length === 1)
            {
                partialDateModel = new PartialDateModel(Number(dateParts[0]), null, null);
            }
        }
        return partialDateModel;
    }

    private convertObject(from: PartialDate): PartialDateModel
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