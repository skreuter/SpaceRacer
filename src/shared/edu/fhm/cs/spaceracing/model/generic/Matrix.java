package edu.fhm.cs.spaceracing.model.generic;

/**
 * @author Stefan Kreuter
 *
 */
public class Matrix
{
	private final double[][] matrix;
	
	public Matrix()
	{
		matrix = new double[3][3];
	}
	
	/**
	 * Definiert eine 3x3 Matrix anhand der gegebenen Vektoren.
	 * 
	 * @param x Erste Reihe
	 * @param y Zweite Reihe
	 * @param z Dritte Reihe
	 */
	public Matrix(Vector x, Vector y, Vector z)
	{
		matrix = new double[][]{{x.getX(), x.getY(), x.getZ()},
								{y.getX(), y.getY(), y.getZ()},
								{z.getX(), z.getY(), z.getZ()}};
	}
	
	public Vector multiplicate(Vector vector)
	{
		return new Vector(vector.scalarProduct(getRowVector(0)), 
				vector.scalarProduct(getRowVector(1)), 
				vector.scalarProduct(getRowVector(2)));
	}
	
	public Vector getRowVector(int row)
	{
		assert row >= 0 && row < 3;
		
		return new Vector(matrix[row][0], matrix[row][1], matrix[row][2]);
	}
	
	public Vector getColumnVector(int line)
	{
		assert line >= 0 && line < 3;
		
		return new Vector(matrix[0][line], matrix[1][line], matrix[2][line]);
	}
	
	public void setRowVector(int row, Vector vec)
	{
		assert row >= 0 && row < 3;
		
		matrix[row][0] = vec.getX();
		matrix[row][1] = vec.getY();
		matrix[row][2] = vec.getZ();
	}
	
	public void setColumnVector(int col, Vector vec)
	{
		assert col >= 0 && col < 3;
		
		matrix[0][col] = vec.getX();
		matrix[1][col] = vec.getY();
		matrix[2][col] = vec.getZ();
	}
	
	public double[] asArray()
	{
		return new double[]{
				matrix[0][0], matrix[0][1], matrix[0][2],
				matrix[1][0], matrix[1][1], matrix[1][2],
				matrix[2][0], matrix[2][1], matrix[2][2] };
	}
	
	public Matrix invertedMatrix()
	{
		int nz = 3;
		double q;
		double[][] matrixCopy = new double[nz][nz];
		double[][] result = new double[nz][nz];
		
		// Matrix kopieren
		for(int i = 0; i < nz; i++)
			for(int j = 0; j < nz; j++)
				matrixCopy[i][j] = matrix[i][j];
				
		// Einheitsmatrix initialisieren
		for(int i = 0; i < nz; i++)
			for(int j = 0; j < nz; j++)
				if(i == j)
					result[i][j] = 1;
				else
					result[i][j] = 0;

		
		
		for (int j = 0; j < nz; j++)
		{
			// Diagonalenfeld normalisieren
			q = matrixCopy[j][j];
			if (q == 0)
			{
				//Gewährleisten, daß keine 0 in der Diagonale steht
				for (int i = j + 1; i < nz; i++)
				{
					// Suche Reihe mit Feld <> 0 und addiere dazu
					if (matrixCopy[i][j] != 0)
					{
						for(int k = 0; k < nz; k++)
						{
							matrixCopy[j][k]+=matrixCopy[i][k];
							result[j][k]+=result[i][k];
						}
						q = matrixCopy[j][j];
	                    		break;
	                }
	            }
			}
			if (q != 0)
			{
	            // Diagonalen auf 1 bringen
				for(int k=0;k<nz;k++)
				{
					matrixCopy[j][k]=matrixCopy[j][k]/q;
					result[j][k]=result[j][k]/q;
				}
			}
			// Spalten außerhalb der Diagonalen auf 0 bringen
			for (int i = 0 ; i< nz ; i++)
			{
				if (i != j )
				{
					q = matrixCopy[i][j];
					for(int k=0;k<nz;k++)
					{
						result[i][k]-=q*result[j][k];
						matrixCopy[i][k]-=q*matrixCopy[j][k];
					}
				}
			}
		}
		for(int i=0;i<nz;i++)
			for(int j=0;j<nz;j++)
				if(matrixCopy[i][j]!=((i==j)?1:0))
					System.out.println("OMGWTFBBQ");
	
		Matrix resultMatrix = new Matrix();
		
		for(int i=0;i<nz;i++) 
			for(int j = 0; j < nz; j++)
				resultMatrix.matrix[i][j]=result[i][j];

		return resultMatrix;
	}
	
	public String toString()
	{
		return getRowVector(0) +  "," + getRowVector(1) + "," + getRowVector(2);		
	}
}
