export default class PartialDateModel
{
    private _year: number;
    private _yearValid: boolean;

    private _month?: number;
    private _monthValid?: boolean;

    private _day?: number;
    private _dayValid?: boolean;

    constructor(year: number, month?: number, day?: number)
    {
        this._year = year;

        if (month)
        {
            this._month = month;
        }

        if (day)
        {
            this._day = day;
        }
    }

    get year(): number
    {
        return this._year;
    }

    set year(value: number)
    {
        this._year = value;
    }

    get month(): number
    {
        return this._month;
    }

    set month(value: number)
    {
        this._month = value;
    }

    get day(): number
    {
        return this._day;
    }

    set day(value: number)
    {
        this._day = value;
    }

    get yearValid(): boolean
    {
        return this._yearValid;
    }

    set yearValid(value: boolean)
    {
        this._yearValid = value;
    }

    get monthValid(): boolean
    {
        return this._monthValid;
    }

    set monthValid(value: boolean)
    {
        this._monthValid = value;
    }

    get dayValid(): boolean
    {
        return this._dayValid;
    }

    set dayValid(value: boolean)
    {
        this._dayValid = value;
    }

    public isValidPartialDate()
    {
        return this._yearValid && this._monthValid && this._dayValid;
    }
}