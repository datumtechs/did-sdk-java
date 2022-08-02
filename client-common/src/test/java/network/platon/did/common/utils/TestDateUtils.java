package network.platon.did.common.utils;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

public class TestDateUtils {

	@Test
	public void test_getNoMillisecondTimeStampString() {
		Assert.assertNotNull(DateUtils.getNoMillisecondTimeStampString());
		Assert.assertNotNull(DateUtils.getNoMillisecondTimeStamp());
		Assert.assertNotNull(DateUtils.getNoMillisecondTimeStampInt256());
		Assert.assertNotNull(DateUtils.getCurrentTimeStamp());
		Assert.assertNotNull(DateUtils.getCurrentTimeStampInt256());
		Assert.assertNotNull(DateUtils.getCurrentTimeStampString());
		Assert.assertNotNull(DateUtils.getTimestamp(new Date()));
		Assert.assertNotNull(DateUtils.getTimestamp(new Date().getTime()));
	}

	@Test
	public void test_change() throws ParseException {
		String date = "2020-07-14 17:29:00";
		Assert.assertNotNull(DateUtils.converDateToTimeStamp(date));
		String utc = DateUtils.convertNoMillisecondTimestampToUtc(DateUtils.getNoMillisecondTimeStamp());
		Assert.assertNotNull(utc);
		date = DateUtils.getTimestamp(new Date());
		Assert.assertNotNull(DateUtils.convertStringToDate(date));
		Assert.assertNotNull(DateUtils.convertUtcDateToNoMillisecondTime(utc));
		Assert.assertNotNull(DateUtils.convertUtcDateToTimeStamp(utc));
	}
}
