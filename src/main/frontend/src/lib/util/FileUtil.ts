import {saveAs} from "file-saver";

export default class FileUtil
{
	// ==========================================================================
	// Public Class Methods
	// ==========================================================================

	/**
	 * get a file's data as a base64 string
	 * @param file - the file to get the base64 data from
	 * @return promise that resolves to base64 string data
	 */
	public static getFileDataBase64(file: File): Promise<string>
	{
		const fileReader = new FileReader();
		fileReader.readAsDataURL(file);

		return new Promise((resolve, reject) =>
		{
			fileReader.onload = () => resolve((fileReader.result as string).split(",")[1]);
			fileReader.onerror = (error) => reject(error);
		})
	}

	/**
	 * present the user with a file upload dialog
	 * @param acceptTypes - the types of files to accept
	 * @return - a promise that resolves to the list of files selected.
	 */
	public static async uploadFile(acceptTypes: string[]): Promise<File[]>
	{
		const inputEl = this.createFileUploadInput(acceptTypes);

		// really janky. Basically append input, click it with js and wait for file selection.
		document.body.append(inputEl);
		const promise = new Promise<File[]>((resolve, reject) =>
		{
			inputEl.onchange = () =>
			{
				const files: File[] = []
				for (let i = 0; i < inputEl.files.length; i++)
				{
					files.push(inputEl.files[i]);
				}

				resolve(files);
				inputEl.remove();
			}

			inputEl.onblur = () =>
			{
				if (inputEl !== document.activeElement)
				{
					resolve([]);
					inputEl.remove();
				}
			}
		});

		inputEl.focus();
		inputEl.click();

		return promise;
	}

	/**
	 * Save a file to the users computer
	 * @param name - the name of the file
	 * @param type - the mime type of the file
	 * @param base64Data - the base64 data of the file.
	 */
	public static saveFile(name: string, type: string, base64Data: string)
	{
		// download and convert to byte array
		const byteString = atob(base64Data);
		const bytes = new Array(byteString.length);
		for (let i = 0; i < byteString.length; i++)
		{
			bytes[i] = byteString.charCodeAt(i);
		}

		// build blob
		const blob = new Blob([new Uint8Array(bytes)], {type: type});

		// save to users computer
		saveAs(blob, name);
	}

	/**
	 * extract file extension form file name or file object
	 * @param file - file object or file name to extract file extension from
	 * @return the file extension or null if the file name contains no extension.
	 */
	public static getFileExtension(file: string | File): string
	{
		if (file instanceof File)
		{
			file = file.name;
		}

		return [file.match(/\.[\w]+$/)]
			.flat()
			.reduce((extension, match) => match, null);
	}

	// ==========================================================================
	// Protected Class Methods
	// ==========================================================================

	/**
	 * create a hidden input suitable for file uploads
	 * @param acceptTypes - the types of files the input should accept
	 * @protected
	 */
	protected static createFileUploadInput(acceptTypes: string[]): HTMLInputElement
	{
		// Ya I know this is stupid but this is how the web currently works.
		const inputEl = document.createElement("input");
		inputEl.accept = acceptTypes.join(',');
		inputEl.type = "file"
		//inputEl.hidden = true;
		inputEl.multiple = true;
		inputEl.style.width = "0";
		inputEl.style.height = "0";
		inputEl.style.overflow = "hidden";

		return inputEl;
	}
}