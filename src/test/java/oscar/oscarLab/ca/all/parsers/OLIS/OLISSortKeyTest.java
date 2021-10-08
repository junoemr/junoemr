package oscar.oscarLab.ca.all.parsers.OLIS;

import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OLISSortKeyTest
{
	@Test
	public void testComparator_null()
	{
		Comparator<OLISSortKey> comparator = OLISSortKey.getKeyComparator();

		int result = comparator.compare(null, null);
		assertEquals(0, result);

		OLISSortKey sortKeyN = new OLISSortKey(null, null, null, null, null);
		result = comparator.compare(sortKeyN, null);
		assertTrue("object should be before null", firstArgBefore(result));

		result = comparator.compare(null, sortKeyN);
		assertTrue("object should be before null", firstArgAfter(result));
	}

	@Test
	public void testComparator_MsgKey()
	{
		Comparator<OLISSortKey> comparator = OLISSortKey.getKeyComparator();
		OLISSortKey sortKeyA = new OLISSortKey("AAA", null, null, null, null);
		OLISSortKey sortKeyB = new OLISSortKey("BBB", null, null, null, null);
		OLISSortKey sortKeyN = new OLISSortKey(null, null, null, null, null);


		int result = comparator.compare(sortKeyA, sortKeyA);
		assertEquals(0, result);

		result = comparator.compare(sortKeyA, sortKeyB);
		assertTrue("A should be before B", firstArgBefore(result));

		result = comparator.compare(sortKeyB, sortKeyA);
		assertTrue("A should be before B", firstArgAfter(result));

		result = comparator.compare(sortKeyA, sortKeyN);
		assertTrue("A should be before null", firstArgBefore(result));
	}

	@Test
	public void testComparator_OLISKey()
	{
		Comparator<OLISSortKey> comparator = OLISSortKey.getKeyComparator();
		OLISSortKey sortKeyA = new OLISSortKey(null, "AAA", null, null, null);
		OLISSortKey sortKeyB = new OLISSortKey(null, "BBB", null, null, null);
		OLISSortKey sortKeyN = new OLISSortKey(null, null, null, null, null);


		int result = comparator.compare(sortKeyA, sortKeyA);
		assertEquals(0, result);

		result = comparator.compare(sortKeyA, sortKeyB);
		assertTrue("A should be before B", firstArgBefore(result));

		result = comparator.compare(sortKeyB, sortKeyA);
		assertTrue("A should be before B", firstArgAfter(result));

		result = comparator.compare(sortKeyA, sortKeyN);
		assertTrue("A should be before null", firstArgBefore(result));
	}

	@Test
	public void testComparator_AltNameKey()
	{
		Comparator<OLISSortKey> comparator = OLISSortKey.getKeyComparator();
		OLISSortKey sortKeyA = new OLISSortKey(null, null, "AAA", null, null);
		OLISSortKey sortKeyB = new OLISSortKey(null, null, "BBB", null, null);
		OLISSortKey sortKeyN = new OLISSortKey(null, null, null, null, null);


		int result = comparator.compare(sortKeyA, sortKeyA);
		assertEquals(0, result);

		result = comparator.compare(sortKeyA, sortKeyB);
		assertTrue("A should be before B", firstArgBefore(result));

		result = comparator.compare(sortKeyB, sortKeyA);
		assertTrue("A should be before B", firstArgAfter(result));

		result = comparator.compare(sortKeyA, sortKeyN);
		assertTrue("A should be before null", firstArgBefore(result));
	}

	@Test
	public void testComparator_SubIdKey()
	{
		Comparator<OLISSortKey> comparator = OLISSortKey.getKeyComparator();
		OLISSortKey sortKeyA = new OLISSortKey(null, null, null, "AAA", null);
		OLISSortKey sortKeyB = new OLISSortKey(null, null, null, "BBB", null);
		OLISSortKey sortKeyN = new OLISSortKey(null, null, null, null, null);


		int result = comparator.compare(sortKeyA, sortKeyA);
		assertEquals(0, result);

		result = comparator.compare(sortKeyA, sortKeyB);
		assertTrue("A should be before B", firstArgBefore(result));

		result = comparator.compare(sortKeyB, sortKeyA);
		assertTrue("A should be before B", firstArgAfter(result));

		result = comparator.compare(sortKeyA, sortKeyN);
		assertTrue("A should be before null", firstArgBefore(result));
	}

	@Test
	public void testComparator_relDateKey()
	{
		Comparator<OLISSortKey> comparator = OLISSortKey.getKeyComparator();
		OLISSortKey sortKeyA = new OLISSortKey(null, null, null, null,
				ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
		OLISSortKey sortKeyB = new OLISSortKey(null, null, null, null,
				ZonedDateTime.of(2000, 2, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
		OLISSortKey sortKeyN = new OLISSortKey(null, null, null, null, null);


		int result = comparator.compare(sortKeyA, sortKeyA);
		assertEquals(0, result);

		result = comparator.compare(sortKeyA, sortKeyB);
		assertTrue("A should be before B", firstArgBefore(result));

		result = comparator.compare(sortKeyB, sortKeyA);
		assertTrue("A should be before B", firstArgAfter(result));

		result = comparator.compare(sortKeyA, sortKeyN);
		assertTrue("A should be before null", firstArgBefore(result));
	}

	@Test
	public void testComparator_MsgKeyPriority()
	{
		Comparator<OLISSortKey> comparator = OLISSortKey.getKeyComparator();
		OLISSortKey sortKeyA = new OLISSortKey("AAA", "ZZZ", "ZZZ", "ZZZ", null);
		OLISSortKey sortKeyB = new OLISSortKey("BBB", "BBB", "BBB", "BBB", null);
		OLISSortKey sortKeyBB = new OLISSortKey("BBB", "ZZZ", "ZZZ", "ZZZ", null);

		int result = comparator.compare(sortKeyA, sortKeyB);
		assertTrue("A should be before B", firstArgBefore(result));

		result = comparator.compare(sortKeyB, sortKeyBB);
		assertTrue("Duplicated msg key should decide ordering on next olis key comparison", firstArgBefore(result));
	}

	@Test
	public void testComparator_OlisKeyPriority()
	{
		Comparator<OLISSortKey> comparator = OLISSortKey.getKeyComparator();
		OLISSortKey sortKeyA = new OLISSortKey(null, "AAA", "ZZZ", "ZZZ", null);
		OLISSortKey sortKeyB = new OLISSortKey(null, "BBB", "BBB", "BBB", null);
		OLISSortKey sortKeyBB = new OLISSortKey(null, "BBB", "ZZZ", "ZZZ", null);

		int result = comparator.compare(sortKeyA, sortKeyB);
		assertTrue("A should be before B", firstArgBefore(result));

		result = comparator.compare(sortKeyB, sortKeyBB);
		assertTrue("Duplicated olis key should decide ordering on altName key comparison", firstArgBefore(result));
	}

	@Test
	public void testComparator_AltKeyPriority()
	{
		Comparator<OLISSortKey> comparator = OLISSortKey.getKeyComparator();
		OLISSortKey sortKeyA = new OLISSortKey(null, null, "AAA", "ZZZ", null);
		OLISSortKey sortKeyB = new OLISSortKey(null, null, "BBB", "BBB", null);
		OLISSortKey sortKeyBB = new OLISSortKey(null, null, "BBB", "ZZZ", null);

		int result = comparator.compare(sortKeyA, sortKeyB);
		assertTrue("A should be before B", firstArgBefore(result));

		result = comparator.compare(sortKeyB, sortKeyBB);
		assertTrue("Duplicated alt key should decide ordering on sub-id key comparison", firstArgBefore(result));
	}

	@Test
	public void testComparator_subIdKeyPriority()
	{
		Comparator<OLISSortKey> comparator = OLISSortKey.getKeyComparator();
		OLISSortKey sortKeyA = new OLISSortKey(null, null, null, "AAA", null);
		OLISSortKey sortKeyB = new OLISSortKey(null, null, null, "BBB",
				ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
		OLISSortKey sortKeyBB = new OLISSortKey(null, null, null, "BBB",
				ZonedDateTime.of(2000, 2, 1, 0, 0, 0, 0, ZoneId.systemDefault()));

		int result = comparator.compare(sortKeyA, sortKeyB);
		assertTrue("A should be before B", firstArgBefore(result));

		result = comparator.compare(sortKeyB, sortKeyBB);
		assertTrue("Duplicated subId key should decide ordering on date key comparison", firstArgBefore(result));
	}

	// simple method to make logic easier to read
	private boolean firstArgBefore(int result)
	{
		return result < 0;
	}

	// simple method to make logic easier to read
	private boolean firstArgAfter(int result)
	{
		return result > 0;
	}
}
