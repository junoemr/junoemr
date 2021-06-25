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

    public isValidPartialDate()
    {
        let validYear = false;
        let validMonth = false;
        let validDay = false;

        let yearRegex = new RegExp("[1-2][0-9][0-9][0-9]");
        let monthRegex = new RegExp("[0-1][0-9]");
        let dayRegex = new RegExp("[0-3][0-9]");

        if (this.allFieldsEmpty)
        {
            return true;
        }

        if (this._year)
        {
            if (this._year.toString().match(yearRegex))
            {
                validYear = true;
            }
        }
        if (this._year && this._month)
        {
            if (this._year.toString().match(yearRegex))
            {
                validYear = true;
            }
            if (this._month.toString().match(monthRegex))
            {
                validMonth = true;
            }
        }
        if (this._year && this._month && this._day)
        {
            if (this._year.toString().match(yearRegex))
            {
                validYear = true;
            }
            if (this._month.toString().match(monthRegex))
            {
                validMonth = true;
            }
            if (this._day.toString().match(dayRegex))
            {
                validDay = true;
            }
        }

        if (validYear)
        {
            if (!(this._month))
            {
                validMonth = true;
            }
            if (!this._day)
            {
                validDay = true;
            }
        }
        if (validYear && validMonth)
        {
            if (!(this._day))
            {
                validDay = true;
            }
        }

        return validYear && validMonth && validDay;
    }

    public allFieldsEmpty()
    {
        return !(this._year) && !(this._month) && !(this._day);
    }
}





