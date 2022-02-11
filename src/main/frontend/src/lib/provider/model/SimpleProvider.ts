/**
 * a simple representation of a provider record. not meant as a full model
 */
export default class SimpleProvider
{
	private _id: string;
	private _firstName: string;
	private _lastName: string;
	private _ohipNumber: string;

	constructor(id: string = null)
	{
		this.id = id;
	}

	get displayName(): string
	{
		return this.lastName + ', ' + this.firstName;
	}

	/**
	 * getters and setters
	 */

	get id(): string
	{
		return this._id;
	}

	set id(value: string)
	{
		this._id = value;
	}

	get firstName(): string
	{
		return this._firstName;
	}

	set firstName(value: string)
	{
		this._firstName = value;
	}

	get lastName(): string
	{
		return this._lastName;
	}

	set lastName(value: string)
	{
		this._lastName = value;
	}

	get ohipNumber(): string
	{
		return this._ohipNumber;
	}

	set ohipNumber(value: string)
	{
		this._ohipNumber = value;
	}
}