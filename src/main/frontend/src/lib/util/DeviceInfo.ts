import {BotInfo, BrowserInfo, detect, NodeInfo, ReactNativeInfo, SearchBotDeviceInfo} from "detect-browser";

export default class DeviceInfo
{
	get browserInfo(): BrowserInfo | SearchBotDeviceInfo | BotInfo | NodeInfo | ReactNativeInfo
	{
		return detect();
	}

	get isChrome(): boolean
	{
		return (this.browserInfo.name === "chrome");
	}

	get isFirefox(): boolean
	{
		return (this.browserInfo.name === "firefox");
	}

	get autocompleteOffValue(): string
	{
		if(this.isChrome)
		{
			return "chrome-off";
		}
		return "off";
	}
};