/**
 * a simple representation of a provider record. not meant as a full model
 */
export default class SimpleProvider
{
	private _id: string;
	private _firstName: string;
	private _lastName: string;
	private _ohipNumber: string;

	constructor(id: string = null, lastName: string = null, firstName: string = null, ohip: string = null)
	{
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.ohipNumber = ohip;
	}

	public static fromDisplayNameAndOhip(nameToSplit: string, ohip: string = null): SimpleProvider
	{
		if(!nameToSplit)
		{
			return null;
		}
		let provider = new SimpleProvider();
		if(!nameToSplit.includes(","))
		{
			provider.lastName = nameToSplit;
		}
		else
		{
			provider.lastName = nameToSplit.split(",")[0];
			provider.firstName = nameToSplit.split(",")[1];
		}
		provider.ohipNumber = ohip;
		return provider;
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