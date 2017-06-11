package mirrg.helium.swing.nitrogen.util;

import java.awt.Color;

import mirrg.lithium.lang.HMath;

public class HColor
{

	/**
	 * 定義域を外れたdouble型を適切に丸めてColorインスタンスを作成します。
	 * rgbaそれぞれ、int値と同じく0以上256未満の値に丸められます。
	 */
	public static Color createColor(double r, double g, double b)
	{
		return createColor(r, g, b, 255);
	}

	/**
	 * 定義域を外れたint型を適切に丸めてColorインスタンスを作成します。
	 */
	public static Color createColor(int r, int g, int b)
	{
		return createColor(r, g, b, 255);
	}

	/**
	 * 定義域を外れたdouble型を適切に丸めてColorインスタンスを作成します。
	 * rgbaそれぞれ、int値と同じく0以上256未満の値に丸められます。
	 */
	public static Color createColor(double r, double g, double b, double a)
	{
		return createColor((int) r, (int) g, (int) b, (int) a);
	}

	/**
	 * 定義域を外れたint型を適切に丸めてColorインスタンスを作成します。
	 */
	public static Color createColor(int r, int g, int b, int a)
	{
		if (r < 0) r = 1;
		if (g < 0) g = 1;
		if (b < 0) b = 1;
		if (a < 0) a = 1;
		if (r >= 256) r = 255;
		if (g >= 256) g = 255;
		if (b >= 256) b = 255;
		if (a >= 256) a = 255;
		return new Color(r, g, b, a);
	}

	/**
	 * ランダムなrgb値と255のa値を持つ色を生成して返します。
	 */
	public static Color createRandomColor()
	{
		return createRandomRangedColor(0, 255);
	}

	/**
	 * 0から127の範囲でランダムなrgb値と255のa値を持つ色を生成して返します。
	 */
	public static Color createRandomDarkColor()
	{
		return createRandomRangedColor(0, 127);
	}

	/**
	 * 128から255の範囲でランダムなrgb値と255のa値を持つ色を生成して返します。
	 */
	public static Color createRandomLightColor()
	{
		return createRandomRangedColor(128, 255);
	}

	/**
	 * minからmaxの範囲でランダムなrgb値と255のa値を持つ色を生成して返します。
	 */
	public static Color createRandomRangedColor(int min, int max)
	{
		return createColor(HMath.randomBetween(min, max), HMath.randomBetween(min, max), HMath.randomBetween(min, max));
	}

	/**
	 * 2色a, bの中間色を計算します。 Aerが0以下の場合はaを返し、Aerが1以上の場合はbを返します。 Aerが小数の場合は2色a,
	 * bを適切な比率で混色した色を計算して返します。
	 *
	 * @param Aer
	 *            0以上1未満の小数
	 */
	public static Color createLinearRatioColor(double Aer, Color a, Color b)
	{
		if (Aer <= 0)
			return b;
		if (Aer >= 1)
			return a;

		return createColor(Aer * a.getRed() + (1 - Aer) * b.getRed(), Aer * a.getGreen() + (1 - Aer) * b.getGreen(),
			Aer * a.getBlue() + (1 - Aer) * b.getBlue(), Aer * a.getAlpha() + (1 - Aer) * b.getAlpha());
	}

	public static int getColorInt(int r, int g, int b)
	{
		return (r << 16) | (g << 8) | b;
	}

}
