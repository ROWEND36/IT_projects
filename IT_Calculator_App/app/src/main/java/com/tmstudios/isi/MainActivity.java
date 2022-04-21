package com.tmstudios.isi;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.*;

public class MainActivity extends Activity 
{
	TextView numView1,numView2,result;
	double num1,num2;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		numView1 = findViewById(R.id.number1);
		numView2 = findViewById(R.id.number2);
		result = findViewById(R.id.result);
    }
	public void setAnswer(double answer)
	{
		result.setText(String.format("%.4f", answer));
	}

	public void clear(View v){
		numView1.setText("");
		numView2.setText("");
		result.setText("");
	}
	public void setOperands()
	{
		try
		{
			num1 = Double.parseDouble(numView1.getText().toString());
			num2 = Double.parseDouble(numView2.getText().toString());
		}
		catch (Exception e)
		{
			num1 = 0;
			num2 = 0;
		}
	}
	public void add(View v)
	{
		setOperands();
		setAnswer(num1 + num2);
	}
	public void subtract(View v)
	{
		setOperands();
		setAnswer(num1 - num2);
	}
	public void divide(View v)
	{
		setOperands();
		try
		{
			setAnswer(num1 / num2);
		}
		catch (Exception e)
		{
			setAnswer(0);
		}
	}
	public void times(View v)
	{
		setOperands();
		setAnswer(num1 * num2);
	}

	@Override
	protected void onResume()
	{
		// TODO: Implement this method
		super.onResume();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}
	
}
