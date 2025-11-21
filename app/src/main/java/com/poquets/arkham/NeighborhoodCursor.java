/*
 * This file is part of Arkham Companion.
 *
 *  Arkham Companion is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Arkham Companion is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Arkham Companion.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.poquets.arkham;

import java.util.ArrayList;

import android.database.AbstractCursor;

public class NeighborhoodCursor extends AbstractCursor {
	
	private ArrayList<Neighborhood> blah;
	
	public NeighborhoodCursor(ArrayList<Neighborhood> neis)
	{
		blah = neis;
	}

	@Override
	public String[] getColumnNames() {
		return new String[] { "_ID", "Left", "Right" };
	}

	@Override
	public int getCount() {
		return (blah.size()+1)/2;
	}

	@Override
	public double getDouble(int arg0) {
		if(arg0 == 0)
			return getPosition();
		return 0;
	}

	@Override
	public float getFloat(int arg0) {
		if(arg0 == 0)
			return getPosition();
		return 0;
	}

	@Override
	public int getInt(int arg0) {
		if(arg0 == 0)
		{
			return getPosition();
		}
		return 0;
	}

	@Override
	public long getLong(int arg0) {
		if(arg0 == 0)
			return getPosition();
		return 0;
	}

	@Override
	public short getShort(int arg0) {
		if(arg0 == 0)
			return (short)getPosition();
		return 0;
	}
	
	public Neighborhood getNeighborhood(int columnIdx)
	{
		int pos = getPosition();
		android.util.Log.d("NeighborhoodCursor", "getNeighborhood called - columnIdx: " + columnIdx + ", position: " + pos + ", blah.size: " + blah.size());
		
		Neighborhood ret;
		if(columnIdx == 1)
		{
			int index = pos * 2;
			if (index < blah.size()) {
				ret = blah.get(index);
				android.util.Log.d("NeighborhoodCursor", "Returning neighborhood from index " + index + ": " + (ret != null ? ret.toString() + " (ID: " + ret.getID() + ")" : "null"));
			} else {
				android.util.Log.e("NeighborhoodCursor", "Index " + index + " out of bounds!");
				ret = null;
			}
		}
		else
		{
			int index = pos * 2 + 1;
			if(index < blah.size())
			{
				ret = blah.get(index);
				android.util.Log.d("NeighborhoodCursor", "Returning neighborhood from index " + index + ": " + (ret != null ? ret.toString() + " (ID: " + ret.getID() + ")" : "null"));
			}
			else
			{
				android.util.Log.d("NeighborhoodCursor", "Index " + index + " out of range, returning null");
				ret = null;
			}
		}
		return ret;
	}

	@Override
	public String getString(int arg0) {
		if( arg0 == 1)
		{
			
			return blah.get(getPosition()*2).toString();
		}
		else if(arg0 == 2)
		{
			if(getPosition()*2+1 < blah.size())
			{
				return blah.get(getPosition()*2+1).toString();
			}
			else
			{
				return "";
			}
		}
		else
		{
			return String.valueOf(getPosition());
		}
		
	}

	@Override
	public boolean isNull(int arg0) {
		
		return blah.get(arg0) == null;
	}

}
