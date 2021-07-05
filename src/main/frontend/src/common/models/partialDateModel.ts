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

    get


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
            return false;
        }

        return (this._year.toString().match(yearRegex));
    }

    public isValidMonth()
    {
        let monthRegex = new RegExp("[0-1][0-9]");

        if (this._year && !this._month && this._day)
        {
            return false;
        }

        if (!this._month)
        {
            return true;
        }

        return (this._month.toString().match(monthRegex) && this._month >= 1 && this._month <= 12);
    }

    public isValidDay()
    {
        let dayRegex = new RegExp("[0-3][0-9]");

        if (!this._day)
        {
            return true;
        }

        return (this._day.toString().match(dayRegex) && this._day >= 1 && this._day <= 31);
    }

    public isValidPartialDate()
    {
        let validDay = this.isValidDay();
        let validMonth = this.isValidMonth();
        let validYear = this.isValidYear();

        return validYear && validMonth && validDay;
    }

    public allFieldsEmpty()
    {
        return !(this._year) && !(this._month) && !(this._day);
    }
}