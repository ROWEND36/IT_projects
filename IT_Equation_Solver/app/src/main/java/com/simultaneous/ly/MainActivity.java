package com.simultaneous.ly;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.util.*;
import android.text.*;
import android.text.method.*;
import android.content.res.*;

public class MainActivity extends Activity 
{
	//Where we display results
	TextView resultView;
	//Where we get input
	GridLayout matrix;
	//Controls the matrix
	BaseAdapter matrixAdapter;
	//Our values in a one-dimensional array read from left to right, top to bottom
	double[] values;
	//The current dimension
	int selected;
	//Used to persist text
	boolean isLoaded;
	//The variables associated with each column
	public static final MatrixSize[] matrixSizes = new MatrixSize[]{
		new MatrixSize(2, 2),
		new MatrixSize(3, 3),
		new MatrixSize(4, 4),
		new MatrixSize(5, 5),
		new MatrixSize(6, 6),
		new MatrixSize(7, 7)
	};
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        isLoaded = false;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		if (savedInstanceState != null)
		{
			values = savedInstanceState.getDoubleArray("values");
			selected = Math.min(savedInstanceState.getInt("selected"), matrixSizes.length - 1);
			try
			{
				logState("values " + matrixSizes[selected].print(values));
			}
			catch (Exception e)
			{
				//There are many valid reasons why this can happen
			}

		}
		initSpinner((Spinner)findViewById(R.id.mainDimension));
		initEquations((GridLayout)findViewById(R.id.mainEquations));
		resultView = findViewById(R.id.mainResult);
		//Allow scrolling on results view
		resultView.setMovementMethod(new ScrollingMovementMethod());
    }

	public String getVarName(int col)
	{
		if (matrixSizes[selected].cols <= 3)
		{
			return col == 0 ?"x": col == 1 ?"y": "z";
		}
		return String.valueOf("abcdefgh".charAt(col));
	}
	public void showResult(String result)
	{
		resultView.setText(result);
	}

	public void clear(View v)
	{
		resultView.setText("");
		values = new double[values.length];
		matrixAdapter.notifyDataSetChanged();
	}

	/** @callback */
	public void compute(View v)
	{
		//Solve using cramer's rule
		MatrixSize matrixSize = matrixSizes[selected];
		try
		{
			//Compute the determinant
			double det = matrixSize.getDeterminant(values);
			StringBuffer resultText = new StringBuffer();
			resultText.append("Determinant: ").append(det).append('\n');

			if (det == 0)
			{
				//Singular matrices cannot be solved with cramer's rule
				showResult("Singular matrix!!!");
				return;
			}

			//Using the determinant compute each of the values
			double[] temp = new double[values.length];
			for (int i=0;i < matrixSize.cols;i++)
			{
				if (i != 0)
					resultText.append(",\n");
				resultText.append(getVarName(i));
				resultText.append(" = ");
				//Swap the column with the results column
				matrixSize.swapOut(i, values, temp);
				//Compute the deteminant
				double detX = matrixSize.getDeterminant(temp);
				//The result is detX/det
				resultText.append(detX / det);
			}
			showResult(resultText.toString());
		}
		catch (Exception e)
		{
			//Stop app from crashing due to Arithmetic errors
			e.printStackTrace();
			showResult("Unknown error");
		}
	}

	//Setup the chooser for dimensions
	public void initSpinner(Spinner m)
	{
		m.setAdapter(new ArrayAdapter<MatrixSize>(this, R.layout.dimension, matrixSizes));
		m.setSelection(selected);
		if (values == null || values.length != matrixSizes[selected].length)
		{
			values = new double[matrixSizes[selected].length];
		}
		m.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
				@Override
				public void onNothingSelected(AdapterView<?> p1)
				{
					p1.setSelection(0);
					setSelection(0);
				}
				private void setSelection(int index)
				{
					selected = index;
					if (values == null || values.length != matrixSizes[selected].length)
					{
						values = new double[matrixSizes[selected].length];
					}
					updateGrid();
					matrixAdapter.notifyDataSetChanged();
				}
				@Override
				public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
				{
					setSelection(p3);
				}
			});
	}
	public void initEquations(GridLayout v)
	{
		matrix = v;
		matrixAdapter = new MatrixAdapter();
		GridRenderer.setAdapter(matrix, matrixAdapter);
		updateGrid();
	}
	public void updateGrid()
	{
		if (matrix.getColumnCount() > matrixSizes[selected].cols + 1)
		{
			matrix.removeAllViews();
		}
		matrix.setColumnCount(matrixSizes[selected].cols + 1);
	}

	//Android Lifecycle callbacks
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putDoubleArray("values", values);
		outState.putInt("selected", selected);
	}
	@Override
	protected void onPause()
	{
		super.onPause();
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		isLoaded = true;
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
		{

		}
	}

	//Utility method for debugging
	public static void logState(String state)
	{
		Log.i("state", state);
	}

	public class Tag
	{
		int position;
		MatrixSize dimen;
		ValueWatcher m;
	}

	//Utility method to catch the error thrown by Double.parseDouble
	public double getValue(Editable v)
	{
		try
		{
			return  Double.parseDouble(v.toString());
		}
		catch (Exception e)
		{
			return 0;

		}
	}
	public class MatrixAdapter extends BaseAdapter
	{

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			//Used to determine the position of the view on screen
			MatrixSize matrixSize = matrixSizes[selected];
			//A {Tag} stores the data of a view across layout changes
			Tag tag;
			View v;
			if (convertView == null)
			{
				v = getLayoutInflater().inflate(R.layout.grid_item, parent, false);
				tag = null;
			}
			else
			{
				v = convertView;
				//Find out if we can reuse convertView
				tag = (Tag)convertView.getTag();
				if (tag != null)
				{
					if (tag.position == position &&

						tag.dimen == matrixSize)
					{
						//Check text value is correct
						EditText coeff = v.findViewById(R.id.grid_itemCoeff);						
						if (getValue(coeff.getText()) != values[position])
						{
							if (values[position] != 0)
								coeff.setText(new Double(values[position]).toString());
							else coeff.setText("");
						}
						return convertView;
					}
				}
			}
			EditText coeff = v.findViewById(R.id.grid_itemCoeff);

			if (tag == null)
			{
				tag = new Tag();
				v.setTag(tag);
				tag.m = new ValueWatcher(tag);
				coeff.addTextChangedListener(tag.m);
			}
			tag.position = position;
			tag.dimen = matrixSize;


			//Fill in the views variable name/ show equals to sign
			final int col = position % (matrixSize.cols + 1);
			final boolean isLast = col == matrixSize.cols;
			TextView varName = v.findViewById(R.id.grid_itemVariable);
			TextView equalsToSign = v.findViewById(R.id.grid_itemEquals);
			if (isLast)
			{
				equalsToSign.setVisibility(equalsToSign.VISIBLE);
				varName.setVisibility(varName.GONE);
			}
			else
			{
				varName.setText(getVarName(col));
				varName.setVisibility(varName.VISIBLE);
				equalsToSign.setVisibility(equalsToSign.GONE);
			}

			//Update value
			Double value = new Double(values[selected]);
			if (value > 0)
				coeff.setText(value.toString());
			else coeff.setText("");

			return v;
		}
		@Override
		public int getCount()
		{
			MatrixSize matrixSize = matrixSizes[selected];
			return matrixSize.length;
		}

		@Override
		public Double getItem(int p1)
		{
			return null;
		}

		@Override
		public long getItemId(int p1)
		{
			return 0;
		}
		public boolean hasStableIds()
		{
			return false;
		}
	}
	public class ValueWatcher implements TextWatcher
	{
		private Tag tag;
		public ValueWatcher(Tag tag)
		{
			this.tag = tag;
		}
		@Override
		public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
		{
		}

		@Override
		public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
		{
		}

		@Override
		public void afterTextChanged(Editable p1)
		{
			if (MainActivity.this.isLoaded)
			{
				values[tag.position] = getValue(p1);
				try
				{
					showResult(matrixSizes[selected].print(values));
				}
				catch (Exception e)
				{}
			}
		}
	}
}
