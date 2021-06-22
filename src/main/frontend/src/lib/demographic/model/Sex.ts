export enum Sex {
	Male = "M",
	Female = "F",
}

export function sexToHuman(sex: Sex): string
{
	switch (sex)
	{
		case Sex.Male:
			return "Male";
		case Sex.Female:
			return "Female";
	}
}