package com.simultaneous.ly;

public class MatrixSize
{
	/* Helper class for handling Matrices
	 * A MatrixSize handles operations on a matrix
	 * The actual matrix is stored in a separate array
	 * for easy serialization etc
	 * The storing order is given as
	 a b c   x
	 d e f = y  => [a,b,c,d,x,d,e,f,y,g,h,i,z]
	 g h i   z
	 This is convenient for both layout and computation
	 */

	public final int rows,cols,length;
	public MatrixSize(int rows, int cols)
	{
		this.rows = rows;
		this.cols = cols;

		//The length of the array needed to store this matrix and it's result
		this.length = rows * (cols + 1);
	}
	public String print(double[] values) throws Exception
	{
		if (values.length != this.length)
		{
			throw new Exception("Wrong dimension for values");
		}
 		StringBuffer p = new StringBuffer();
		p.append("[");
		//print left hand side
		int equalsToIndex = (int)Math.floor((this.rows - 1) / 2) * (this.cols + 1) + this.cols;
		for (int i=0;i < values.length;i++)
		{
			if (isOnLeftHandSide(i))
			{
				if (i > 0)
				{
					p.append(",");
					if (column(i) == 0)
					{
						p.append("\n ");
					}
					else p.append(" ");
				}
			}
			else
			{
				if (i == this.length - 1)p.append(" ]");
				else p.append(",");
				if (i == equalsToIndex)p.append("  =  ");
				else p.append("     ");
			}
			p.append(String.format("%4.4f", values[i]));

		}
		return p.toString();
	}
	public int column(int position)
	{
		return position % (this.cols + 1);
	}

	public boolean isOnLeftHandSide(int position)
	{
		return (position % (this.cols + 1)) != this.cols;
	}
	public String toString()
	{
		return String.format("%d x %d", rows, cols);
	}
	/* Cramers rule: replace a given column with another. */

	public double[] swapOut(int column, double[] src, double[] dest) throws Exception
	{
		if (src.length != this.length)
		{
			throw new Exception("Wrong dimension for values");
		}

		for (int i=0;i < this.length;i++)
		{
			int col = i % (this.cols + 1);
			int offset = i - col;
			if (col == this.cols)
			{
				col = column;
			}
			else if (col == column)
			{
				col = this.cols;
			}

			int index = offset + col;
			dest[index] = src[i];
		}
		return dest;
	}
	//Get the determinant of the values
	public double getDeterminant(double[] values) throws Exception
	{
		if (this.rows != this.cols)
		{
			throw new Exception(String.format("Cannot solve rectangular matrix %s", this.toString()));
		}
		return _getDeterminant(values, this.cols);
	}


	private double _getDeterminant(double[] values, int covered_column)
	{
		if (this.cols == 2)
		{
			/**
			 * Simplest case 2 by 2 matrix
			 * 0 1  2
			 * 3 4  5
			 */
			if (covered_column == 2)
			{
				return values[0] * values[4] - values[3] * values[1];
			}
			else if (covered_column == 1)
			{
				return values[0] * values[5] - values[3] * values[2]; 
			}
			else
			{
				return values[1] * values[5] - values[4] * values[2];
			}
		}
		double sum=0;
		double[] innerVals = new double[this.length - this.cols - this.rows];
		
		//While MatrixSize is very lightweight, 
		//one can still save memory by reusing the already saved
		//objects in MainActivity.matrixSizes
		MatrixSize inner = new MatrixSize(this.cols - 1, this.rows - 1);


		int offset=0;
		//Create a smaller matrix for computing inner determinants
		for (int i=this.cols + 1;i < this.length;i++)
		{
			if (column(i) == covered_column)
			{
				continue;
			}
			innerVals[offset] = values[i];
			offset++;
		}
		//Then compute determinants for each of the rows using the smaller matrix
		offset = 0;
		for (int j=0,sign=1;j <= this.cols;j++)
		{
			if (j == covered_column)
			{
				continue;
			}
			sum += sign * values[j] * inner._getDeterminant(innerVals, offset);
			offset ++;
			sign = -sign;
		}
		return sum;
	}
}
