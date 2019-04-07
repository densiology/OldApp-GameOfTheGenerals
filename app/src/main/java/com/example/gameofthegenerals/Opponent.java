package com.example.gameofthegenerals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public class Opponent
{
	
	public <T> T[] concat(T[] first, T[] second)
	{
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
	
	// returns the tags in order (gen2star, col, flag,...)
	public String[] setupTags()
	{
		if (BoardActivity.difficulty == 1)
		{
			String[] array = new String[]{ "sgt", "lieut2nd", "lieut1st", "capt", "major", "collt", 
					   					   "col", "gen1star", "gen2star", "gen3star", "gen4star", 
					   					   "gen5star", "spy", "spy", "private", "private", "private", 
					   					   "private", "private", "private", "flag" };
			Collections.shuffle(Arrays.asList(array));
			return array;
		}
		else if (BoardActivity.difficulty == 2)
		{
			// put the top 3 generals, 1 spy, 2 privates, and the rest to the front.
			String[] array = new String[]{ "gen2star", "gen1star", "spy", "private", "private", "private", 
				    						"private", "sgt", "flag" };
			Collections.shuffle(Arrays.asList(array));
			String[] array2 = new String[]{ "gen5star", "gen4star", "gen3star", "spy", "private", "private", 
										   "col", "collt", "major", "capt", "lieut1st", "lieut2nd" };
			Collections.shuffle(Arrays.asList(array2));
			String[] combined = concat(array, array2);
			return combined;
		}
		else if (BoardActivity.difficulty == 3)
		{
			// put the 5 generals, 2 spies, 3 privates, col and collt to the front
			String[] array = new String[]{ "private", "private", "private", "major", "capt", "lieut1st",
										   "lieut2nd", "sgt", "flag" };
			Collections.shuffle(Arrays.asList(array));
			String[] array2 = new String[]{ "gen5star", "gen4star", "gen3star", "gen2star", "gen1star",
										    "spy", "spy", "private", "private", "private", "col", "collt" };
			Collections.shuffle(Arrays.asList(array2));
			String[] combined = concat(array, array2);
			return combined;
		}
		return null;
	}
	
	// what the AI actually chooses is (1) the unit to move and (2) at what direction will it move
	// returns the chosen view ID and the unit_direction (432323 -> col_up)
	// the parameter is the available moves in (view ID) -> unit_up-down-left-right (i.e. 4652143 -> gen5star_0101)
	public HashMap<Integer, String> moveFromOpponent(HashMap<Integer, String> availableMoves)
	{
		if (BoardActivity.difficulty == 1)
		{
			// randomly pick an item in Map
			Random random = new Random();
			List<Integer> keys = new ArrayList<Integer>(availableMoves.keySet());
			int randomKey = keys.get(random.nextInt(keys.size()));
			String value = availableMoves.get(randomKey);
			// on that Map item, extract the unit (gen5star)
			String unit = value.substring(0, value.indexOf("_"));
			// on that Map item, extract the directions (0101)
			String directions = value.substring(value.indexOf("_")+1);
			// store the available moves (down, right) to an array.
			List<String> strMoves = new ArrayList<String>();
			if (directions.charAt(0) == '1') { strMoves.add("up"); }
			if (directions.charAt(1) == '1') { strMoves.add("down"); }
			if (directions.charAt(2) == '1') { strMoves.add("left"); }
			if (directions.charAt(3) == '1') { strMoves.add("right"); }
			// pick a random item on that array
			String direction = strMoves.get(random.nextInt(strMoves.size()));
			// formulate and return the Map item
			HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
			hashMap.put(randomKey, unit + "_" + direction);
			return hashMap;
		}
		else if (BoardActivity.difficulty == 2 || BoardActivity.difficulty == 3)
		{
			HashMap<Integer, String> pickedHashMap = new HashMap<Integer, String>();
			// grab the items
			List<String> values = new ArrayList<String>(availableMoves.values());
			// separate the items in array1 and array2 (according to power)
			ArrayList<String> array1 = new ArrayList<String>(); // less powerful
			ArrayList<String> array2 = new ArrayList<String>(); // more powerful
			for (String value : values)
			{
				if (value.contains("flag") || value.contains("private") || value.contains("sgt") || value.contains("lieut2nd") || 
					value.contains("lieut1st") || value.contains("capt") || value.contains("major"))
				{
					array1.add(value);
				}
				else
				{
					array2.add(value);
				}
			}

			// pick array2 80% of the time, and array1 20% of the time (on a ratio of 5:1)
			double d = Math.random();
			if (d < 0.2 || array2.isEmpty())
			{	
				Collections.shuffle(array1);
				String chosen = array1.get(0);
				for (Entry<Integer, String> entry : availableMoves.entrySet())
				{
					if (entry.getValue().equals(chosen))
					{
						pickedHashMap.put(entry.getKey(), chosen);
					}
				}
			}
			else
			{
				Collections.shuffle(array2);
				String chosen = array2.get(0);
				for (Entry<Integer, String> entry : availableMoves.entrySet())
				{
					if (entry.getValue().equals(chosen))
					{
						pickedHashMap.put(entry.getKey(), chosen);
					}
				}
			}
			List<Integer> keys = new ArrayList<Integer>(pickedHashMap.keySet());
			int key = keys.get(0);
			String value = pickedHashMap.get(key);
			// on that Map item, extract the unit (gen5star)
			String unit = value.substring(0, value.indexOf("_"));
			// on that Map item, extract the directions (0101)
			String directions = value.substring(value.indexOf("_")+1);
			// store the available moves (down, right) to an array.
			List<String> strMoves = new ArrayList<String>();
			if (directions.charAt(0) == '1') { strMoves.add("up"); }
			if (directions.charAt(1) == '1') { strMoves.add("down"); }
			if (directions.charAt(2) == '1') { strMoves.add("left"); }
			if (directions.charAt(3) == '1') { strMoves.add("right"); }
			// pick a random item on that array
			Random random = new Random();
			String direction = strMoves.get(random.nextInt(strMoves.size()));
			// formulate and return the Map item
			HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
			hashMap.put(key, unit + "_" + direction);
			return hashMap;
		}
		return null;
	}
}
