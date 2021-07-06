/*
* Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
* This software is published under the GPL GNU General Public License.
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
*
* This software was written for
* CloudPractice Inc.
* Victoria, British Columbia
* Canada
*/

// position of input label
export enum LABEL_POSITION
{
	TOP = "juno-input-label-top",
	LEFT = "juno-input-label-left",
}

// styles of juno inputs
export enum JUNO_STYLE
{
	DEFAULT = "juno-style-default",
	GREY = "juno-style-grey",
	DRACULA = "juno-style-dracula",
}

// use as, JUNO_STYLE + BACKGROUND_STYLE
export enum JUNO_BACKGROUND_STYLE
{
	PRIMARY = "-background",
	SECONDARY = "-background-secondary",
}

export enum JUNO_BUTTON_COLOR_PATTERN
{
	DEFAULT = "default",
	COLORED = "colored",
	DARK_HOVER = "dark-hover",
	TRANSPARENT = "transparent",
	FILL = "fill",
	FILL_LIGHT = "fill-light",
	FILL_DARK = "fill-dark",
}

export enum JUNO_BUTTON_COLOR
{
	PRIMARY = "primary",
	SECONDARY = "secondary",
	GREYSCALE_LIGHT = "greyscale-light",
	GREYSCALE_DARK = "greyscale-dark",
	GREYSCALE_DARKEST = "greyscale-darkest",
	INFO = "info",
	SUCCESS = "success",
	WARNING = "warning",
	DANGER = "danger",
}

export enum JUNO_TAB_TYPE
{
	NORMAL = "juno-tab-normal",
	SWIM_LANE = "juno-tab-swim-lane"
}

export enum JUNO_INPUT_MODAL_TYPE
{
	TEXT = "text",
	SELECT = "select",
}