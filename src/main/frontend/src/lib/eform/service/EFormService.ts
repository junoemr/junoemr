import EFormInstance from "../model/EFormInstance";

export default class EFormService
{
	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * print an eform to pdf
	 * @param eformInstance - the eform instance to print
	 * @return promise that resolves to base64 encoded pdf data.
	 */
	public printEForm(eformInstance: EFormInstance): Promise<string>
	{

	}
}