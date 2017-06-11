package mirrg.helium.swing.nitrogen.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class HAWT
{

	/**
	 * 1ピクセルの枠が付いた文字列を色を指定して描画する。
	 */
	public static void drawBoldString(Graphics2D g, String str, int x, int y, Color borderColor, Color fillColor)
	{
		g.setColor(borderColor);
		g.drawString(str, x + 1, y);
		g.drawString(str, x - 1, y);
		g.drawString(str, x, y + 1);
		g.drawString(str, x, y - 1);
		g.setColor(fillColor);
		g.drawString(str, x, y);
	}

	/**
	 * 0x00RRGGBB・透過なし
	 */
	public static BufferedImage createBufferedImageFromIntArray(int[] data, int width, int height)
	{
		DataBuffer dataBuffer = new DataBufferInt(data, width * height);

		WritableRaster raster = Raster.createPackedRaster(dataBuffer, width, height, width,
			new int[] {
				0x00ff0000, 0x0000ff00, 0x000000ff,
			}, null);

		// Raster.createPackedRaster(dataBuffer, width, height, 32, new
		// Point());
		// colorModel = new ComponentColorModel(
		// ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB), false, false,
		// Transparency.OPAQUE, DataBuffer.TYPE_INT);

		ColorModel colorModel = new DirectColorModel(24, 0x00ff0000, 0x0000ff00, 0x000000ff, 0x0);

		return new BufferedImage(colorModel, raster, false, null);
	}

}
