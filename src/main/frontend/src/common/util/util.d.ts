// TS type binding for Juno util.
// This allows you to use JunoUtil in TS code without @ts-ignore
declare const Juno: {
	Common: {
		// @ts-ignore
		Util: Juno.Common.Util,
	}
	// @ts-ignore
	Validations: Juno.Validations,
};