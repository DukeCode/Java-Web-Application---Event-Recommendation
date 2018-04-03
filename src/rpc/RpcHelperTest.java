package rpc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import entity.Event;
import entity.Event.EventBuilder;

public class RpcHelperTest {

	@Test
	public void testGetJSONArrayCornerCases() throws JSONException {
		Set<String> category = new HashSet<String>();
		category.add("category one");
		
		List<Event> listEvent = new ArrayList<Event>();
		JSONArray jsonArray = new JSONArray();
		JSONAssert.assertEquals(jsonArray, RpcHelper.getJSONArray(listEvent), true);

		Event one = new EventBuilder().setEventId("one").setLatitude(33.33).setRating(5).setCategories(category).setLongitude(33.33).build();
		Event two = new EventBuilder().setEventId("two").setLatitude(33.33).setRating(5).setCategories(category).setLongitude(33.33).build();
		listEvent.add(one);
		listEvent.add(two);
		
		jsonArray.put(one.toJSONObject());
		jsonArray.put(two.toJSONObject());	
		JSONAssert.assertEquals(jsonArray, RpcHelper.getJSONArray(listEvent), true);
		
		Event empty = new EventBuilder().build();
		listEvent.add(empty);
		jsonArray.put(empty.toJSONObject());
		JSONAssert.assertEquals(jsonArray, RpcHelper.getJSONArray(listEvent), true);
	}
}
