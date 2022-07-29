/**
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
package oscar.oscarLab.ca.all.parsers;

import org.junit.Assert;
import org.junit.Test;
import oscar.oscarLab.ca.all.model.EmbeddedDocument;
import oscar.oscarLab.ca.all.util.Utilities;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractMessageHandlerTestBase<T extends MessageHandler>
{
	protected abstract Map<MessageHandler, String> getExpectedAccessionMap();
	protected abstract Map<MessageHandler, Integer> getExpectedDocumentCountMap();
	protected abstract Map<MessageHandler, List<String>> getExpectedRoutingIdsMap();

	protected static List<MessageHandler> loadResourceFile(ClassLoader classLoader, String messageType, String name) throws IOException
	{
		File file = new File(Objects.requireNonNull(classLoader.getResource(name)).getFile());
		List<String> messages = Utilities.separateMessages(file);
		return messages.stream().map((msg) -> Factory.getHandler(messageType, msg)).collect(Collectors.toList());
	}

	@Test
	public void testAccessionNumbers()
	{
		Map<MessageHandler, String> accessionMap = getExpectedAccessionMap();
		for(Map.Entry<MessageHandler, String> entry : accessionMap.entrySet())
		{
			MessageHandler handler = entry.getKey();
			String expectedAccession = entry.getValue();
			String identifier = getHandlerId(handler);

			Assert.assertEquals("[" + identifier + "] Incorrect accession number", expectedAccession, handler.getAccessionNumber());
		}

	}

	@Test
	public void testEmbeddedDocuments()
	{
		Map<MessageHandler, Integer> documentCountMap = getExpectedDocumentCountMap();
		for(Map.Entry<MessageHandler, Integer> entry : documentCountMap.entrySet())
		{
			MessageHandler handler = entry.getKey();
			Integer expectedCount = entry.getValue();
			String identifier = getHandlerId(handler);

			List<EmbeddedDocument> embeddedDocuments = handler.getEmbeddedDocuments();

			Assert.assertEquals("[" + identifier + "] Incorrect number of embedded documents", (int) expectedCount, embeddedDocuments.size());
		}
	}

	@Test
	public void testProviderRoutingIds()
	{
		Map<MessageHandler, List<String>> routingIdsMap = getExpectedRoutingIdsMap();
		for(Map.Entry<MessageHandler, List<String>> entry : routingIdsMap.entrySet())
		{
			MessageHandler handler = entry.getKey();
			List<String> expectedRoutingIds = entry.getValue();
			List<String> actualRouteIds = handler.getDocNums();
			String identifier = getHandlerId(handler);

			for(String expectedRouteId: expectedRoutingIds)
			{
				Assert.assertTrue("[" + identifier + "] expected routeId '" + expectedRouteId + "' not present in the actual results\n" +
								"  Expected: " + expectedRoutingIds + "\n" +
								"    Actual: " + actualRouteIds,
						actualRouteIds.contains(expectedRouteId));
			}
			for(String actualRouteId: actualRouteIds)
			{
				Assert.assertTrue("[" + identifier + "] actual routeId '" + actualRouteId + "' not present in the expected results.\n" +
								"  Expected: " + expectedRoutingIds + "\n" +
								"    Actual: " + actualRouteIds,
						expectedRoutingIds.contains(actualRouteId));
			}
			Assert.assertEquals("[" + identifier + "] Incorrect number of provider routing ids", expectedRoutingIds.size(), actualRouteIds.size());
		}
	}

	private String getHandlerId(MessageHandler handler)
	{
		return handler.getUniqueIdentifier() + ":" + handler.getUniqueVersionIdentifier();
	}
}