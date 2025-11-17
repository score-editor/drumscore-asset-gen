package xyz.arwhite;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.imageio.ImageIO;

public class DSEAssetGen {

	public static Color dsNoteInk = new Color(0x373d3f);
	
	public static double fontFactorForEM = 0.25f;

	public static final char timeSig0 = '\ue080';
	public static final double timeSig0SWX = 0.08f;
	public static final double timeSig0SWY = -1.0f;
	public static final double timeSig0NEX = 1.8f;
	public static final double timeSig0NEY = 1.004f;

	public static final char timeSig1 = '\ue081';
	public static final double timeSig1SWX = 0.08f;
	public static final double timeSig1SWY = -1.0f;
	public static final double timeSig1NEX = 1.256f;
	public static final double timeSig1NEY = 1.004f;

	public static final char timeSig2 = '\ue082';
	public static final double timeSig2SWX = 0.08f; 
	public static final double timeSig2SWY = -1.028f; 
	public static final double timeSig2NEX = 1.704f;  
	public static final double timeSig2NEY = 1.016f;

	public static final char timeSig3 = '\ue083';
	public static final double timeSig3SWX = 0.08f; 
	public static final double timeSig3SWY = -1.004f; 
	public static final double timeSig3NEX = 1.604f;  
	public static final double timeSig3NEY = 1.996f;

	public static final char timeSig4 = '\ue084';
	public static final double timeSig4SWX = 0.08f; 
	public static final double timeSig4SWY = -1.0f; 
	public static final double timeSig4NEX = 1.8f;  
	public static final double timeSig4NEY = 1.004f;

	public static final char timeSig5 = '\ue085';
	public static final double timeSig5SWX = 0.08f; 
	public static final double timeSig5SWY = -1.004f; 
	public static final double timeSig5NEX = 1.532f;  
	public static final double timeSig5NEY = 0.984f;

	public static final char timeSig6 = '\ue086';
	public static final double timeSig6SWX = 0.08f; 
	public static final double timeSig6SWY = -0.996f; 
	public static final double timeSig6NEX = 1.656f;  
	public static final double timeSig6NEY = 1.004f;

	public static final char timeSig7 = '\ue087';
	public static final double timeSig7SWX = 0.08f; 
	public static final double timeSig7SWY = -1.0f; 
	public static final double timeSig7NEX = 1.684f;  
	public static final double timeSig7NEY = 0.996f;

	public static final char timeSig8 = '\ue088';
	public static final double timeSig8SWX = 0.08f; 
	public static final double timeSig8SWY = -1.036f; 
	public static final double timeSig8NEX = 1.664f;  
	public static final double timeSig8NEY = 1.036f;

	public static final char timeSig9 = '\ue089';
	public static final double timeSig9SWX = 0.08f; 
	public static final double timeSig9SWY = -0.996f; 
	public static final double timeSig9NEX = 1.656f;  
	public static final double timeSig9NEY = 1.004f;

	public static final char timeSigCommon = '\ue08a';
	public static final double timeSigCommonSWX = 0.02f; 
	public static final double timeSigCommonSWY = -0.996f; 
	public static final double timeSigCommonNEX = 1.696f;  
	public static final double timeSigCommonNEY = 1.004f;

	public static final char timeSigCutCommon = '\ue08b';
	public static final double timeSigCutCommonSWX = 0.0f; 
	public static final double timeSigCutCommonSWY = -1.436f; 
	public static final double timeSigCutCommonNEX = 1.672f;  
	public static final double timeSigCutCommonNEY = 1.444f;
	


	public static final char[] timeSigs = {
			timeSig0, timeSig1, timeSig2, timeSig3, timeSig4, 
			timeSig5, timeSig6, timeSig7, timeSig8, timeSig9
	};
	
	public static final char[] commonSigs = {
			timeSigCommon, timeSigCutCommon
	};
	
	private record TSNum(int upper, int lower) {};

	private List<TSNum> ts = List.of(
			new TSNum(2,4),
			new TSNum(3,4),
			new TSNum(4,4),
			new TSNum(6,8),
			new TSNum(9,8),
			new TSNum(12,8),
			new TSNum(2,2)
			);

	public static final char metNoteHalfUp = '\ueca3';
	public static final char metNoteQuarterUp = '\ueca5';
	public static final char metNote8thUp = '\ueca7';
	public static final char space = '\u0020';
	public static final char metAugmentationDot = '\uecb7';
	public static final char[] tempos = { metNoteHalfUp, metNoteQuarterUp, metNote8thUp };
	public static final String[] tempoNames = { "tempo-2", "tempo-4", "tempo-8" };
	
	private String dirName;
	private Font font;
	
	private float tsFontSize = 46.0f;
	private float tempoFontSize = 32.0f;

	private void render() throws FontFormatException, IOException {

		setupOutputDir();
		setupFont();

		// create broader worker image
		BufferedImage image = new BufferedImage(128, 64, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2D = image.createGraphics();

		// create the usual timesig icons
		var maxWidth = getMaxTSWidth(g2D);
		
		ts.forEach(t ->{
			// var w = this.getTSRenderWidth(g2D, t.upper, t.lower); 
			var w = maxWidth;
			w += 4;

			// - set up buff img 64pt high, max width of element plus 2 x margin width
			BufferedImage img = new BufferedImage(w, 64, BufferedImage.TYPE_INT_ARGB);
			renderTSImage(img.createGraphics(), t.upper, t.lower, w);
			
			// - write glyphs to 64pt image
			try {
				writePNG(img,dirName + "timesig-"+t.upper+"-"+t.lower+"-64.png");
				
				// for height 32 - the actual icon size, the 64 we wrote is the 2x
				var wh32 = w / 2;
				writeScaledPNG(img, dirName + "timesig-"+t.upper+"-"+t.lower, wh32, 32 );
				
				// for height 40 - the 1.25x size
				var wh40 = wh32 + (wh32 / 4);
				writeScaledPNG(img, dirName + "timesig-"+t.upper+"-"+t.lower, wh40, 40 );
				
				// for height 48 - the 1.5x size
				var wh48 = wh32 + (wh32 / 2);
				writeScaledPNG(img, dirName + "timesig-"+t.upper+"-"+t.lower, wh48, 48 );
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		// create the common / cut-common ones
		for ( int i = 0; i < commonSigs.length; i++ ) {
			// var w = getSingleGlyphWidth(g2D,commonSigs[i]);
			var w = maxWidth;
			w += 4;
			
			BufferedImage img = new BufferedImage(w, 64, BufferedImage.TYPE_INT_ARGB);
			renderSingleGlyphImage(img.createGraphics(), commonSigs[i], w);
			writePNG(img, dirName + "timesig-"+(i == 0 ? "common" : "cut-common")+"-64.png");
			
			// for height 32 - the actual icon size, the 64 we wrote is the 2x
			var wh32 = w / 2;
			writeScaledPNG(img, dirName + "timesig-"+(i == 0 ? "common" : "cut-common"), wh32, 32 );
			
			// for height 40 - the 1.25x size
			var wh40 = wh32 + (wh32 / 4);
			writeScaledPNG(img, dirName + "timesig-"+(i == 0 ? "common" : "cut-common"), wh40, 40 );
			
			// for height 48 - the 1.5x size
			var wh48 = wh32 + (wh32 / 2);
			writeScaledPNG(img, dirName + "timesig-"+(i == 0 ? "common" : "cut-common"), wh48, 48 );
			
		}
		
		// smaller notes for tempo markings
		font = font.deriveFont(tempoFontSize);
		for ( int i = 0; i < tempos.length; i++ ) {
			var w = getSingleGlyphWidth(g2D, tempos[i]);
			w += 2;
			
			BufferedImage img = new BufferedImage(w, 32, BufferedImage.TYPE_INT_ARGB);
			flexRenderSingleGlyphImage(img.createGraphics(), tempos[i], w, 32, new Color(0,0,0,0));
			writePNG(img, dirName + tempoNames[i]+"-32.png");
			writeScaledPNG(img, dirName + tempoNames[i], w / 2, 16);
			writeScaledPNG(img, dirName + tempoNames[i], (w / 2) + (w / 2 / 4), 20);
			writeScaledPNG(img, dirName + tempoNames[i], (w / 2) + (w / 2 / 2), 24);
		}
		
		// a dotted 8th note for the small tempo markings
		{
			var w = getSingleGlyphWidth(g2D, metNoteQuarterUp);
			var x = getSingleGlyphWidth(g2D, space);
			var z = getSingleGlyphWidth(g2D, metAugmentationDot);
			System.out.println(w + " " + x + " " + z);
			w = w + 9 + z + 2;
			
			char[] glyphs = { metNoteQuarterUp, space, metAugmentationDot };
			BufferedImage img = new BufferedImage(w, 32, BufferedImage.TYPE_INT_ARGB);
			flexRenderListOfGlyphsImage(img.createGraphics(), glyphs, w, 32, new Color(0,0,0,0));
			writePNG(img, dirName + "tempo-4-dot-32.png");
			writeScaledPNG(img, dirName + "tempo-4-dot", w / 2, 16);
			writeScaledPNG(img, dirName + "tempo-4-dot", (w / 2) + (w / 2 / 4), 20);
			writeScaledPNG(img, dirName + "tempo-4-dot", (w / 2) + (w / 2 / 2), 24);
			
		}

	}

	private int getMaxTSWidth(Graphics2D g2D) {
		var frc = g2D.getFontRenderContext();
		char[] topDigits = { timeSig1, timeSig2 };
		GlyphVector gv = font.createGlyphVector(frc, topDigits);
		return (int) gv.getVisualBounds().getWidth();
	}

	private void renderSingleGlyphImage(Graphics2D g2D, char glyph, int width) {
		// white background
		g2D.setColor(Color.WHITE);
		g2D.fillRect(0, 0, width, 64);
		
		// black pen
		g2D.setColor(dsNoteInk);
		
		var frc = g2D.getFontRenderContext();
		
		char[] ga = { glyph };
		GlyphVector gv = font.createGlyphVector(frc, ga);
		
		double tsX = ((width - gv.getVisualBounds().getWidth()) / 2.0f);
		double tsY = 32;
		
		g2D.drawGlyphVector(gv,(float) tsX, (float) tsY);
		
	}
	
	// hoping the above can call this but the y placing might be the issue as above needs halfway
	private void flexRenderSingleGlyphImage(Graphics2D g2D, char glyph, int width, int height, Color bgColor) {
		g2D.setColor(bgColor);
		g2D.fillRect(0, 0, width, height);
		System.out.println(width+" "+height);
		
		// black pen
		g2D.setColor(dsNoteInk);
		
		var frc = g2D.getFontRenderContext();
		
		char[] ga = { glyph };
		GlyphVector gv = font.createGlyphVector(frc, ga);
		
		double tsX = ((width - gv.getVisualBounds().getWidth()) / 2.0f);
		//double tsY = (height / 2.0f);
		//double tsY = (float)height - ((float)height) - gv.getVisualBounds().getHeight();
		double tsY = gv.getVisualBounds().getHeight();
		
		g2D.drawGlyphVector(gv,(float) tsX, (float) tsY);
		
	}
	
	private void flexRenderListOfGlyphsImage(Graphics2D g2D, char[] glyphs, int width, int height, Color bgColor) {
		g2D.setColor(bgColor);
		g2D.fillRect(0, 0, width, height);
		System.out.println("multi "+width+" "+height);
		
		// black pen
		g2D.setColor(dsNoteInk);
		
		var frc = g2D.getFontRenderContext();
		
		GlyphVector gv = font.createGlyphVector(frc, glyphs);
		
		double tsX = ((width - gv.getVisualBounds().getWidth()) / 2.0f);
		double tsY = gv.getVisualBounds().getHeight();
		
		g2D.drawGlyphVector(gv,(float) tsX, (float) tsY);
		
	}
	
	private void renderTSImage(Graphics2D g2D, int upper, int lower, int width) {

		// white background
		g2D.setColor(Color.WHITE);
		g2D.fillRect(0, 0, width, 64);
		
		// black pen
		g2D.setColor(dsNoteInk);
		
		var frc = g2D.getFontRenderContext();

		char[] topDigits = { 0, 0 };
		char[] bottomDigits = { 0, 0 };

		int topDig0 = upper / 10;
		int topDig1 = upper % 10;
		int index = 0;

		if ( topDig0 != 0 ) {
			topDigits[0] = timeSigs[topDig0];
			index++;
		}

		topDigits[index] = timeSigs[topDig1];

		int bottomDig0 = lower / 10;
		int bottomDig1 = lower % 10;
		index = 0;

		if ( bottomDig0 != 0 ) {
			bottomDigits[0] = timeSigs[bottomDig0];
			index++;
		}

		bottomDigits[index] = timeSigs[bottomDig1];

		GlyphVector topgv = font.createGlyphVector(frc, topDigits);
		GlyphVector bottomgv = font.createGlyphVector(frc, bottomDigits);

		double tsTopX = ((width - topgv.getVisualBounds().getWidth()) / 2.0f);
		double tsBottomX = ((width - bottomgv.getVisualBounds().getWidth()) / 2.0f);
		double tsTopY = 32 - (tsFontSize * fontFactorForEM);
		double tsBottomY = 32 + (tsFontSize * fontFactorForEM);

		g2D.drawGlyphVector(topgv,(float) tsTopX, (float) tsTopY);
		g2D.drawGlyphVector(bottomgv,(float) tsBottomX, (float) tsBottomY);
	}

	private int getSingleGlyphWidth(Graphics2D g2D, char glyph) {
		var frc = g2D.getFontRenderContext();
		char[] ga = { glyph };
		GlyphVector gv = font.createGlyphVector(frc, ga);
		return (int) gv.getVisualBounds().getWidth();
	}
	
//	private int getTSRenderWidth(Graphics2D g2D, int upper, int lower) {
//
//		var frc = g2D.getFontRenderContext();
//
//		char[] topDigits = { 0, 0 };
//		char[] bottomDigits = { 0, 0 };
//
//		int topDig0 = upper / 10;
//		int topDig1 = upper % 10;
//		int index = 0;
//
//		if ( topDig0 != 0 ) {
//			topDigits[0] = timeSigs[topDig0];
//			index++;
//		}
//
//		topDigits[index] = timeSigs[topDig1];
//
//		int bottomDig0 = lower / 10;
//		int bottomDig1 = lower % 10;
//		index = 0;
//
//		if ( bottomDig0 != 0 ) {
//			bottomDigits[0] = timeSigs[bottomDig0];
//			index++;
//		}
//
//		bottomDigits[index] = timeSigs[bottomDig1];
//
//		GlyphVector topgv = font.createGlyphVector(frc, topDigits);
//		GlyphVector bottomgv = font.createGlyphVector(frc, bottomDigits);
//
//		return (int) Math.max(topgv.getVisualBounds().getWidth(), bottomgv.getVisualBounds().getWidth());
//
//	}

	private void setupFont() throws FontFormatException, IOException {
		InputStream is = this.getClass().getResourceAsStream("Bravura.otf");
		font = Font.createFont(Font.TRUETYPE_FONT, is);
		font = font.deriveFont(tsFontSize);
	}

	private void setupOutputDir() {
		String sep = System.getProperty("file.separator");
		dirName = System.getProperty("user.home") + sep + "Downloads" + sep + "dse_assets" + sep;
		new File(dirName).mkdirs();
		System.out.println("Target Dir "+dirName);
	}
	
	private void writePNG(BufferedImage img, String filename) throws FileNotFoundException, IOException {
		try (OutputStream out = new BufferedOutputStream(new FileOutputStream(filename))) {
			ImageIO.write(img, "png", out);
		}
	}
	
	private void writeScaledPNG(BufferedImage img64, String prefix, int width, int height) throws FileNotFoundException, IOException {
		var img = img64.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		var bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
	    Graphics2D g2d = bimg.createGraphics();
	    g2d.drawImage(img, 0, 0, null);
	    g2d.dispose();
		
		writePNG(bimg, prefix + "-" + height + ".png");
	}

	public static void main(String[] args) throws FontFormatException, IOException {
		var app = new DSEAssetGen();
		app.render();
	}

}
