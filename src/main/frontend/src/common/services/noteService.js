'use strict';

/*

 Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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

 This software was written for the
 Department of Family Medicine
 McMaster University
 Hamilton
 Ontario, Canada

 */
angular.module("Common.Services").service("noteService", [
	'$http',
	'$httpParamSerializer',
	'$q',
	'junoHttp',
	function(
		$http,
		$httpParamSerializer,
		$q,
		junoHttp)
	{
		var service = {};

		service.apiPath = '../ws/rs/notes';

		service.getNotesFrom = function getNotesFrom(demographicNo, offset, numberToReturn, noteConfig)
		{
			var deferred = $q.defer();

			var config = Juno.Common.ServiceHelper.configHeaders();
			config.params = {
				// noteConfig: noteConfig,
				'offset': offset,
				'numToReturn': numberToReturn
			};

			junoHttp.get(service.apiPath + '/' + encodeURIComponent(demographicNo) + '/all', config).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("noteService::getNotesFrom error", errors);
					deferred.reject("An error occurred while fetching notes");
				});

			return deferred.promise;
		};

		service.saveNote = function saveNote(demographicNo, note)
		{
			var deferred = $q.defer();

			junoHttp.post(service.apiPath + '/' + encodeURIComponent(demographicNo) +
				'/save', note).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("noteService::saveNote error", errors);
					deferred.reject("An error occurred while saving note");
				});

			return deferred.promise;
		};

		service.saveIssueNote = function saveIssueNote(demographicNo, note)
		{
			var deferred = $q.defer();

			$http.post(service.apiPath + '/' + encodeURIComponent(demographicNo) +
				'/saveIssueNote', note).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("noteService::saveIssueNote error", errors);
					deferred.reject("An error occurred while saving issue note");
				});

			return deferred.promise;
		};

		service.getCurrentNote = function getCurrentNote(demographicNo, config)
		{
			var deferred = $q.defer();

			$http.post(service.apiPath + '/' + encodeURIComponent(demographicNo) +
				'/getCurrentNote', config).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("noteService::getCurrentNote error", errors);
					deferred.reject("An error occurred while fetching current note");
				});

			return deferred.promise;
		};

		service.tmpSave = function tmpSave(demographicNo, note)
		{
			var deferred = $q.defer();

			$http.post(service.apiPath + '/' +
				encodeURIComponent(demographicNo) + '/tmpSave', note).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("noteService::tmpSave error", errors);
					deferred.reject("An error occurred while posting tmp save");
				});

			return deferred.promise;
		};

		service.getNoteExt = function getNoteExt(noteId)
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + '/getGroupNoteExt/' + encodeURIComponent(noteId)).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("noteService::getNoteExt error", errors);
					deferred.reject("An error occurred while fetching note ext");
				});

			return deferred.promise;
		};

		service.getIssueNote = function getIssueNote(noteId)
		{
			var deferred = $q.defer();

			junoHttp.get(service.apiPath + '/getIssueNote/' + encodeURIComponent(noteId),
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("noteService::getIssueNote error", errors);
					deferred.reject("An error occurred while fetching issue note");
				});

			return deferred.promise;
		};

		service.getIssueId = function getIssueId(issueCode)
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + '/getIssueId/' + encodeURIComponent(issueCode)).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("noteService::getIssueId error", errors);
					deferred.reject("An error occurred while fetching issue id");
				});

			return deferred.promise;
		};

		service.getTicklerNote = function getTicklerNote(ticklerId)
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + '/ticklerGetNote/' + encodeURIComponent(ticklerId),
			{
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("noteService::getTicklerNote error", errors);
					deferred.reject("An error occurred while fetching tickler note");
				});

			return deferred.promise;
		};

		service.saveTicklerNote = function saveTicklerNote(ticklerNote)
		{
			var deferred = $q.defer();
			$http(
			{
				url: service.apiPath + '/ticklerSaveNote',
				method: "POST",
				data: JSON.stringify(ticklerNote),
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("noteService::saveTicklerNote error", errors);
					deferred.reject("An error occurred while saving tickler note");
				});

			return deferred.promise;
		};

		service.searchIssues = function searchIssues(search, startIndex, itemsToReturn)
		{
			var deferred = $q.defer();

			$http.post(service.apiPath + '/searchIssues?startIndex=' +
				encodeURIComponent(startIndex) + "&itemsToReturn=" +
				encodeURIComponent(itemsToReturn), search).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("noteService::searchIssues error", errors);
					deferred.reject("An error occurred while searching issues");
				});

			return deferred.promise;
		};

		service.getIssue = function getIssue(issueId)
		{
			var deferred = $q.defer();

			$http.post(service.apiPath + '/getIssueById/' + encodeURIComponent(issueId)).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("noteService::getIssue error", errors);
					deferred.reject("An error occurred while fetching issue");
				});

			return deferred.promise;
		};

		service.setEditingNoteFlag = function setEditingNoteFlag(noteUUID, userId)
		{
			var deferred = $q.defer();

			$http.post(service.apiPath + '/setEditingNoteFlag?noteUUID=' +
				encodeURIComponent(noteUUID) + "&userId=" +
				encodeURIComponent(userId)).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("noteService::setEditingNoteFlag error", errors);
					deferred.reject("An error occurred while setting editing note flag");
				});

			return deferred.promise;
		};

		service.checkEditNoteNew = function checkEditNoteNew(noteUUID, userId)
		{
			var deferred = $q.defer();

			$http.post(service.apiPath + '/checkEditNoteNew?noteUUID=' +
				encodeURIComponent(noteUUID) + "&userId=" +
				encodeURIComponent(userId)).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("noteService::checkEditNoteNew error", errors);
					deferred.reject("An error occurred while checking edit note new");
				});

			return deferred.promise;
		};

		service.removeEditingNoteFlag = function removeEditingNoteFlag(noteUUID, userId)
		{
			var deferred = $q.defer();

			$http.post(service.apiPath + '/removeEditingNoteFlag?noteUUID=' +
				encodeURIComponent(noteUUID) + "&userId=" +
				encodeURIComponent(userId)).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("noteService::removeEditingNoteFlag error", errors);
					deferred.reject("An error occurred while removing editing note flag");
				});

			return deferred.promise;
		};

		return service;
	}
]);