'use strict';

/*

    Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for
    CloudPractice Inc.
    Victoria, British Columbia
    Canada

 */

window.Juno = window.Juno || {};
Juno.Common = Juno.Common || {};

Juno.Common.SearchListHelper = function SearchListHelper(defaultParams, searchParams)
{
	var me = this;

	me.searchParams = searchParams || {};
	me.defaults = {
		page: 1,
		perPage: 10,
	};

	me.initSearchParameters = function initSearchParameters()
	{
		var fieldName;

		// Merge default params
		if(Juno.Common.Util.exists(defaultParams))
		{
			for(fieldName in defaultParams)
			{
				if(defaultParams.hasOwnProperty(fieldName))
				{
					me.defaults[fieldName] = defaultParams[fieldName];
				}
			}
		}
	};

	me.getParams = function getParams()
	{
		var outParams = {};
		for(var fieldName in me.searchParams)
		{
			if(me.searchParams.hasOwnProperty(fieldName))
			{
				outParams[fieldName] = me.searchParams[fieldName];
			}
		}
		for(var fieldName in me.defaults)
		{
			if(!outParams.hasOwnProperty(fieldName))
			{
				outParams[fieldName] = me.defaults[fieldName];
			}
		}
		return outParams;
	};

	me.initSearchParameters();
};
