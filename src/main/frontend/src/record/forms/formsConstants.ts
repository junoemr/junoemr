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

export enum FORM_CONTROLLER_STATES
{
		ADD,
		COMPLETED,
		REVISION,
		DELETED,
		MANAGE,
}

export enum FORM_CONTROLLER_SORT_MODES
{
	FORM_NAME = "name",
	ADDITIONAL ="subject",
	MOD_DATE = "date",
	CREATE_DATE = "createDate"
}

export enum FORM_CONTROLLER_FORM_TYPES
{
	EFORM = 'eform',
	FORM ='form'
}

export enum FORM_CONTROLLER_SPECIAL_GROUPS
{
	SELECT_ALL = -1,
	SELECT_EFORM = -2,
	SELECT_FORM = -3
}