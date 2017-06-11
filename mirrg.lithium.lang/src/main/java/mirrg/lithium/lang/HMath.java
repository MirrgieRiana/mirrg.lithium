package mirrg.lithium.lang;

import java.util.Random;

/**
 * 数学的な関数を扱うクラス。
 */
public class HMath
{

	public static int randomBetween(Random random, int min, int max)
	{
		return (int) (random.nextDouble() * (max - min + 1) + min);
	}

	public static int randomBetween(int min, int max)
	{
		return (int) (Math.random() * (max - min + 1) + min);
	}

	/**
	 * log e 10
	 */
	public static final double LOG_10 = Math.log(10);

	/**
	 * log 10 2
	 */
	public static final double LOG_10_2 = Math.log(2) / LOG_10;

	/**
	 * log 10 5
	 */
	public static final double LOG_10_5 = Math.log(5) / LOG_10;

	/**
	 * 与えた数を(1か2か5×10^n)倍の数に切り捨てる。
	 *
	 * @param mspace
	 *            正の数
	 */
	public static double nice(double mspace)
	{
		double log10 = Math.log10(mspace);

		double integerNumber = Math.floor(log10);
		double mod = log10 - integerNumber;

		if (mod > LOG_10_5) {
			mod = LOG_10_5;
		} else if (mod > LOG_10_2) {
			mod = LOG_10_2;
		} else {
			mod = 0;
		}

		return Math.pow(10, integerNumber + mod);
	}

	public static double magnitude2(double re, double im)
	{
		return Math.pow(re, 2) + Math.pow(im, 2);
	}

	public static double magnitude(double re, double im)
	{
		return Math.sqrt(magnitude2(re, im));
	}

	public static boolean isPrime(long a)
	{
		if (a <= 1)
			return false;
		if (a == 2)
			return true;

		double limit = Math.sqrt(a) + 1;

		for (long i = 2; i < limit; i++) {
			if (a % i == 0) {
				return false;
			}
		}

		return true;
	}

	public static double sigmoid(double x, double a)
	{
		return 1 / (1 + Math.exp(-a * x));
	}

	public static double sigmoid(double x)
	{
		return 1 / (1 + Math.exp(-x));
	}

	public static double logit(double x, double a)
	{
		return Math.log(x / (1 - x)) / a;
	}

	public static double logit(double x)
	{
		return Math.log(x / (1 - x));
	}

}
