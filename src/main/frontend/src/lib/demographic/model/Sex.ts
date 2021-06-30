export enum Sex {
	Male = "M",
	Female = "F",
	Transgender = "T",
	Other = "O",
	Undefined = "U",
}

export function sexToHuman(sex: Sex): string
{
	switch (sex)
	{
		case Sex.Male:
			return "Male";
		case Sex.Female:
			return "Female";
		case Sex.Transgender:
			return "Transgender";
		case Sex.Other:
			return "Other";
		case Sex.Undefined:
			return "Undefined";
	}
}