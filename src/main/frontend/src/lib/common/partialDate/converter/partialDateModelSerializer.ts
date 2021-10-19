import PartialDateModel from "../model/partialDateModel";
import AbstractConverter from "../../../conversion/AbstractConverter";

export default class PartialDateModelSerializer extends AbstractConverter<PartialDateModel, string>
{
    convert(from: PartialDateModel): string
    {
        if (!from || !from.year )
        {
            return null;
        }

        const dateParts = [];

        dateParts.push(from.year.toString().padStart(4, "1000"));
        if(from.month)
        {
            dateParts.push(from.month.toString().padStart(2, "0"));
        }
        if(from.day)
        {
            dateParts.push(from.day.toString().padStart(2, "0"));
        }
        return dateParts.join("-");
    }
}