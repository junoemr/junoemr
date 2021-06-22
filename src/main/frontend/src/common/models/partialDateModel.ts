import { PartialDate } from "../../../generated";

export default class PartialDateModel
{
    protected _year: number;
    protected _month?: number;
    protected _day?: number;

    constructor(year: number, month?: number, day?: number)
    {
        this._year = year;
        this._month = month;
        this._day = day;
    }

    public getYear()
    {
        return this._year;
    }
    public setYear(year)
    {
        this._year = year;
    }

    public getMonth()
    {
        return this._month;
    }
    public setMonth(month)
    {
        this._month = month;
    }

    public getDay()
    {
        return this._day;
    }
    public setDay(day)
    {
        this._day = day;
    }

    public isYearOnly()
    {
        if (this._year != null && this._month == null && this._day == null)
        {
            return true;
        }
        return false;
    }

    public isYearMonth()
    {
        if (this._year != null && this._month != null && this._day == null)
        {
            return true;
        }
        return false;
    }

    public isFullDate()
    {
        if (this._year != null && this._month != null && this._day != null)
        {
            return true;
        }
        return false;
    }
}





