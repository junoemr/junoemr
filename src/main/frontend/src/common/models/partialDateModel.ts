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

    public isValidYear()
    {
        let yearRegex = new RegExp("[1-2][0-9][0-9][0-9]");

        if (!this._year)
        {
            return true;
        }

        if (this._year.toString().match(yearRegex))
        {
            return true;
        }

        return false;
    }

    public isValidMonth()
    {
        let monthRegex = new RegExp("[0-1][0-9]");

        if (!this._month)
        {
            return true;
        }

        if (this._year && this.isValidYear() && this._month)
        {
            if (this._month.toString().match(monthRegex) && this._month >= 1 && this._month <= 12)
            {
                return true;
            }
        }

        return false;
    }

    public isValidDay()
    {
        let dayRegex = new RegExp("[0-3][0-9]");

        if (!this._day)
        {
            return true;
        }

        if (this._year && this.isValidYear() && this._month && this.isValidMonth() && this._day)
        {
            if (this._day.toString().match(dayRegex) && this._day >= 1 && this._day <= 31)
            {
                return true;
            }
        }

        return false;
    }

    public isValidPartialDate()
    {
        let validYear = this.isValidYear();
        let validMonth = this.isValidMonth();
        let validDay = this.isValidDay();

        if (this.allFieldsEmpty())
        {
            return true;
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