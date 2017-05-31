'use strict';

window.Juno = window.Juno ||
{};
Juno.Consults = Juno.Consults ||
{};

Juno.Consults.Common = {};

Juno.Consults.Common.sortAttachmentDocs = function sortAttachmentDocs(arrayOfDocs)
{
	arrayOfDocs.sort(function(doc1, doc2)
	{
		if (doc1.documentType < doc2.documentType) return -1;
		else if (doc1.documentType > doc2.documentType) return 1;
		else
		{
			if (doc1.displayName < doc2.displayName) return -1;
			else if (doc1.displayName > doc2.displayName) return 1;
		}
		return 0;
	});
};