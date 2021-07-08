export default class PartialDateModel
{
    private _year: string;
    private _yearValid: boolean;

    private _month?: string;
    private _monthValid?: boolean;

    private _day?: string;
    private _dayValid?: boolean;

    constructor(year: string, month?: string, day?: string)
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

    get year(): string
    {
        return this._year;
    }

    set year(value: string)
    {
        this._year = value;
    }

    get month(): string
    {
        return this._month;
    }

    set month(value: string)
    {
        this._month = value;
    }

    get day(): string
    {
        return this._day;
    }

    set day(value: string)
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