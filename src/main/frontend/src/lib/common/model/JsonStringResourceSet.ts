import StringResourceSet from "./StringResourceSet";
import NoSuchResourceError from "../../error/util/NoSuchResourceError";

export default class JsonStringResourceSet implements StringResourceSet
{
	protected _jsonFileName: string;
	protected _jsonData: any;
	protected _fallbackResourceSet: StringResourceSet;

	// ==========================================================================
	// StringResourceSet implementation
	// ==========================================================================

	/**
	 * create new Json backed string resource set
	 * @param jsonData - loaded json data.
	 * @param jsonFileName - [optional] the file name of the json file. Used for error messages.
	 * @param fallbackResourceSet - [optional] if provided when a string cannot be found for the given key in this resource set the search will fallback to checking the provided set.
	 */
	constructor(jsonData: any, jsonFileName = "", fallbackResourceSet: StringResourceSet = null)
	{
		this._jsonData = jsonData;
		this._jsonFileName = jsonFileName;
		this._fallbackResourceSet = fallbackResourceSet;
	}

	/**
	 * check if the specified key is present in this resource set.
	 * @param key - the key to look for, "ex foo.bar"
	 * @return true / false
	 */
	public hasKey(key: string): boolean
	{
		try
		{
			this.getKeyValue(key);
			return true;
		}
		catch(error)
		{
			if (error instanceof NoSuchResourceError)
			{
				return false;
			}
			else
			{
				throw error;
			}
		}
	}

	/**
	 * get a string from this resource set
	 * @param key - the key to get
	 * @return the string
	 * @throws NoSuchResourceError if the string with specified key is not found.
	 */
	public getString(key: string): string
	{
		return this.getKeyValue(key);
	}

	// ==========================================================================
	// Protected Methods
	// ==========================================================================

	/**
	 * get the value of the key from the jsonData.
	 * @param key - the key to fetch.
	 */
	protected getKeyValue(key: string): string
	{
		if (!key)
		{
			throw new NoSuchResourceError("Cannot fetch resource with null key");
		}

		let currObj = this._jsonData;
		let splitAttributes = key.split(".");
		for (let attr of splitAttributes)
		{
			currObj = currObj[attr];
			if (currObj === undefined)
			{
				break;
			}
		}

		if (!currObj)
		{
			if (this._fallbackResourceSet)
			{
				return this._fallbackResourceSet.getString(key)
			}
			else
			{
				throw new NoSuchResourceError(`Could not find resource with key [${key}] in json file [${this._jsonFileName}]`)
			}
		}
		return currObj;
	}

}