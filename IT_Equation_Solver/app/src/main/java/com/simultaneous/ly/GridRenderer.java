package com.simultaneous.ly;
import android.widget.*;
import android.content.*;
import android.view.*;
import java.util.*;
import android.database.*;

public class GridRenderer
{
	/*Simple Grid rendering chosen for simplicity*/
	static void renderGrid(GridLayout layout, ListAdapter adapter)
	{
		for (int i=0;i < adapter.getCount();i++)
		{
			View convert = layout.getChildAt(i);
			View v = adapter.getView(i, convert, layout);
			if (v == convert)continue;
			layout.removeView(v);
			layout.addView(v, i);
		}
	}
	//Update the grid whenever the data changes, @see {BaseAdapter.notifyDatasetChanged}
	static void setAdapter(final GridLayout layout, final ListAdapter adapter)
	{
		adapter.registerDataSetObserver(new DataSetObserver(){
				public void onChanged()
				{
					GridRenderer.renderGrid(layout, adapter);
				}
			});
		
		GridRenderer.renderGrid(layout,adapter);
	}
	
}

