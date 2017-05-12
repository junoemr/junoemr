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
	'$http', '$q',
	function ($http, $q)
	{
		var service = {};

		service.apiPath = '../ws/rs/notes';

		service.getNotesFrom = function getNotesFrom(demographicNo, offset, numberToReturn, noteConfig)
		{
			var deferred = $q.defer();

			$http.post(service.apiPath + '/' + encodeURIComponent(demographicNo) +
				'/all?offset=' + encodeURIComponent(offset) +
				'&numToReturn=' + encodeURIComponent(numberToReturn),
				noteConfig, Juno.Common.ServiceHelper.configHeaders()).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("noteService::getNotesFrom error", error);
					deferred.reject("An error occurred while fetching notes");
				});

			return deferred.promise;
		};

		service.saveNote = function saveNote(demographicNo, notea)
		{
			var deferred = $q.defer();

			var noteToSave = { encounterNote: notea };
			$http.post(service.apiPath + '/' + encodeURIComponent(demographicNo) +
				'/save', noteToSave).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("noteService::saveNote error", error);
					deferred.reject("An error occurred while saving note");
				});

			return deferred.promise;
		};

		service.saveIssueNote = function saveIssueNote(demographicNo, notea)
		{
			var deferred = $q.defer();

			var note = { noteIssue: notea };
			$http.post(service.apiPath + '/' + encodeURIComponent(demographicNo) +
				'/saveIssueNote', note).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("noteService::saveIssueNote error", error);
					deferred.reject("An error occurred while saving issue note");
				});

			return deferred.promise;
		};

		service.getCurrentNote = function getCurrentNote(demographicNo, config)
		{
			var deferred = $q.defer();

			$http.post(service.apiPath + '/' + encodeURIComponent(demographicNo) +
				'/getCurrentNote', config).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("noteService::getCurrentNote error", error);
					deferred.reject("An error occurred while fetching current note");
				});

			return deferred.promise;
		};

		service.tmpSave = function tmpSave(demographicNo, notea)
		{
			var deferred = $q.defer();

			var noteToSave = { encounterNote: notea };
			$http.post(service.apiPath + '/' +
				encodeURIComponent(demographicNo) + '/tmpSave', noteToSave).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("noteService::tmpSave error", error);
					deferred.reject("An error occurred while posting tmp save");
				});

			return deferred.promise;
		};

		service.getNoteExt = function getNoteExt(noteId)
		{
			var deferred = $q.defer();

			$http.post(service.apiPath + '/getGroupNoteExt/' + encodeURIComponent(noteId)).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("noteService::getNoteExt error", error);
					deferred.reject("An error occurred while fetching note ext");
				});

			return deferred.promise;
		};

		service.getIssueNote = function getIssueNote(noteId)
		{
			var deferred = $q.defer();

			$http.post(service.apiPath + '/getIssueNote/' + encodeURIComponent(noteId)).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("noteService::getIssueNote error", error);
					deferred.reject("An error occurred while fetching issue note");
				});

			return deferred.promise;
		};

		service.getIssueId = function getIssueId(issueCode)
		{
			var deferred = $q.defer();

			$http.post(service.apiPath + '/getIssueId/' + encodeURIComponent(issueCode)).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("noteService::getIssueId error", error);
					deferred.reject("An error occurred while fetching issue id");
				});

			return deferred.promise;
		};

		service.getTicklerNote = function getTicklerNote(ticklerId)
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + '/ticklerGetNote/' + encodeURIComponent(ticklerId),
				{ headers: Juno.Common.ServiceHelper.configHeaders() }).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("noteService::getTicklerNote error", error);
					deferred.reject("An error occurred while fetching tickler note");
				});

			return deferred.promise;
		};

		service.saveTicklerNote = function saveTicklerNote(ticklerNote)
		{
			var deferred = $q.defer();
			$http({
				url: service.apiPath + '/ticklerSaveNote',
				method: "POST",
				data: JSON.stringify(ticklerNote),
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("noteService::saveTicklerNote error", error);
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
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("noteService::searchIssues error", error);
					deferred.reject("An error occurred while searching issues");
				});

			return deferred.promise;
		};

		service.getIssue = function getIssue(issueId)
		{
			var deferred = $q.defer();

			$http.post(service.apiPath + '/getIssueById/' + encodeURIComponent(issueId)).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("noteService::getIssue error", error);
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
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("noteService::setEditingNoteFlag error", error);
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
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("noteService::checkEditNoteNew error", error);
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
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("noteService::removeEditingNoteFlag error", error);
					deferred.reject("An error occurred while removing editing note flag");
				});

			return deferred.promise;
		};

		return service;
	}
]);
