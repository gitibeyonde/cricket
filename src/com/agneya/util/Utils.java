package com.agneya.util;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import java.awt.image.BufferedImageOp;

import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;

import java.awt.image.ImageObserver;
import java.awt.image.Kernel;

import java.awt.image.PixelGrabber;

import java.math.BigDecimal;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;


//import com.agneya.web.Constants;

//import net.sf.javainetlocator.InetAddressLocator;
//import net.sf.javainetlocator.InetAddressLocatorException;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class Utils {

  public static final String restrict_countries[] = {
      "AU", "SW"};

  public Utils() {
  }

  public static double[] integralDivide(double amt, int count) {
    double part[] = new double[count];
    int amount = (int) (amt * 100);
    int int_part = amount / count;
    int remainder = (amount % count);
    for (int i = 0; i < count; i++) {
      part[i] = int_part / 100.00;
    }
    for (int r = remainder, i = 0; r > 0; r--, i++) {
      part[i] += 0.01;
    }
    return part;
  }

  public static double[] integralDivide3Precs(double amt, int count) {
    double part[] = new double[count];
    int amount = (int) (amt * 1000);
    int int_part = amount / count;
    int remainder = (amount % count);
    for (int i = 0; i < count; i++) {
      part[i] = int_part / 1000.00;
    }
    for (int r = remainder, i = 0; r > 0; r--, i++) {
      part[i] += 0.001;
    }
    return part;
  }
  
  public static double getRounded(double val) {
    BigDecimal bg = new BigDecimal(Double.toString(val));
    bg = bg.setScale(2, bg.ROUND_HALF_UP);
    val = bg.doubleValue();
    return val;
  }

  public static String getRoundedString(double val) {
    BigDecimal bg = new BigDecimal(Double.toString(val));
    bg = bg.setScale(2, bg.ROUND_HALF_UP);
    return bg.toString();
  }

  public static String getTruncatedString(double val) {
    BigDecimal bg = new BigDecimal(Double.toString(val));
    bg = bg.setScale(0, bg.ROUND_HALF_DOWN);
    return bg.toString();
  }

  public static String getRoundedDollarCent(double val) {
    int iv = (int) val;
    if (iv * 100 == val * 100) {
      return iv + "";
    }
    else {
      return getRoundedString(val);
    }
  }

  /**public static String getCountry(String ip) throws InetAddressLocatorException {
    Locale l = InetAddressLocator.getLocale(ip);
    return l.getCountry();
  }**/

 /** public static boolean isRestricted(String ip) {
    try {
      String c = Utils.getCountry(ip);
      for (int i = 0; i < restrict_countries.length; i++) {
        if (c.equals(restrict_countries[i])) {
          return true;
        }
      }
    }
    catch (Exception e) {
      //
    }
    return false;
  }**/

  public static String getFormattedNumber(Locale locale, int maxFractionDigits,
                                          String numberIn) throws
      ParseException {
    if (numberIn == null || numberIn.trim().equals("")) {
      return "";
    }
    String numberOut = "";
    NumberFormat numberFormatter = NumberFormat.getInstance();
    numberFormatter.setMaximumFractionDigits(maxFractionDigits);
    numberOut = numberFormatter.format(numberFormatter.parse(numberIn).
                                       doubleValue());
    return numberOut;
  }

  public static String getFormattedNumber(String numberIn) {
    if (numberIn == null || numberIn.trim().equals("")) {
      return "";
    }
    String numberOut = "";
    NumberFormat numberFormatter = NumberFormat.getInstance();
    numberFormatter.setMaximumFractionDigits(2);
    try {
      numberOut = numberFormatter.format(numberFormatter.parse(numberIn).
                                         doubleValue());
    }
    catch (ParseException pe) {
      //pe.printStackTrace();
    }
    return numberOut;
  }

  public static String getFormattedCurrency(String currencyIn) {
    if (currencyIn == null || currencyIn.trim().equals("")) {
      return "";
    }
    String currencyOut = "";
    try {
      Locale locale = Locale.US;
      NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
      NumberFormat numberFormatter = NumberFormat.getInstance();
      currencyOut = currencyFormatter.format(numberFormatter.parse(currencyIn).
                                             doubleValue());
    }
    catch (ParseException pe) {
      //pe.printStackTrace();
    }
    return currencyOut;
  }
  
  public static String getFormattedDate(Calendar calendar){
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd-HH:mm z");
      simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
      return simpleDateFormat.format(calendar.getTime());
  }

  public static Date getDateFromString(String str){
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd-HH:mm z");
      simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date d = new Date();
        try {
                d = simpleDateFormat.parse(str);
        }
        catch(java.text.ParseException e) {
                e.printStackTrace();
        }
        return d;
  }
  
    //Initialize reg ex for email. 
    static String email_expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
    static String alphanumeric_expression = "[a-zA-Z_0-9]+";
    static String[] reserver_words = { "saint", "admin" };

  public static boolean emailFormat(String str){
    boolean isValid = false;
        //Make the comparison case-insensitive.
    Pattern pattern = Pattern.compile(email_expression,Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(str);
    if(matcher.matches()){
        isValid = true;
    }
    return isValid; 
  }
  
    public static boolean alphanumericFormat(String str){
      boolean isValid = false;
      //Make the comparison case-insensitive.
      Pattern pattern = Pattern.compile(alphanumeric_expression,Pattern.UNICODE_CASE);
      Matcher matcher = pattern.matcher(str);
      if(matcher.matches()){
          isValid = true;
      }
      return isValid; 
    }    
    
    public static boolean userNameFormat(String str){
      boolean isValid = false;
      //Make the comparison case-insensitive.
      Pattern pattern = Pattern.compile(alphanumeric_expression,Pattern.UNICODE_CASE);
      Matcher matcher = pattern.matcher(str);
      if(matcher.matches()){
          isValid = true;
      }
      if (!isValid)return false;
      // the name should not contain certain words
      for (int i=0;i<reserver_words.length; i++){
          if (str.contains(reserver_words[i])) return false;
      }
      return true; 
    }
    
    public static boolean validateDob(Date d){
      boolean isValid = false;
      // must be 18 years old or more
      Calendar bd = Calendar.getInstance();
      bd.setTime(d);
      Calendar cd =Calendar.getInstance();
      cd.setTimeInMillis(System.currentTimeMillis());
      
      if (cd.get(Calendar.YEAR) - bd.get(Calendar.YEAR) < 18){
          return false;
      }
      
      return true; 
    }    
    
    
    
      public static BufferedImage resizeTransparentImage(Image imag, int width, int height) { 
          BufferedImage image = toTransparentBufferedImage(imag);
          BufferedImage resizedImage = new BufferedImage(width, height, 
          BufferedImage.TYPE_INT_ARGB); 
          Graphics2D g = resizedImage.createGraphics();
          g.drawImage(image, 0, 0, width, height, null);
          g.dispose();
          return resizedImage; 
      } 
    
    public static BufferedImage resizeImage(Image imag, int width, int height) { 
        BufferedImage image = toBufferedImage(imag);
        BufferedImage resizedImage = new BufferedImage(width, height, 
        BufferedImage.TYPE_INT_ARGB); 
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage; 
    } 
    public static BufferedImage resizeImage(BufferedImage image, int width, int height) { 
        BufferedImage resizedImage = new BufferedImage(width, height, 
        BufferedImage.TYPE_INT_ARGB); 
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage; 
    } 
  
    public static BufferedImage blurImage(BufferedImage image) { 
        float ninth = 1.0f/9.0f;
        float[] blurKernel = { 
        ninth, ninth, ninth,
        ninth, ninth, ninth,
        ninth, ninth, ninth
    
        };
    
        Map map = new HashMap();
    
        map.put(RenderingHints.KEY_INTERPOLATION, 
        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    
    
        map.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    
        map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
        RenderingHints hints = new RenderingHints(map);
        BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, blurKernel), ConvolveOp.EDGE_NO_OP, hints);
        return op.filter(image, null);

    } 
    private static BufferedImage resize(BufferedImage image, int width, int height) { 
        int type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.setComposite(AlphaComposite.Src);
    
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    
    
        g.setRenderingHint(RenderingHints.KEY_RENDERING, 
        RenderingHints.VALUE_RENDER_QUALITY);
    
    
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
        RenderingHints.VALUE_ANTIALIAS_ON);
    
    
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage; 
    } 
    

    
    public static BufferedImage toBufferedImage(Image image) {
            if (image instanceof BufferedImage) {
                return (BufferedImage)image;
            }
        
            image = new ImageIcon(image).getImage();
            BufferedImage bimage = null;
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            try {
                // Determine the type of transparency of the new buffered image
                int transparency = Transparency.OPAQUE;
        
                // Create the buffered image
                GraphicsDevice gs = ge.getDefaultScreenDevice();
                GraphicsConfiguration gc = gs.getDefaultConfiguration();
                bimage = gc.createCompatibleImage(
                    image.getWidth(null), image.getHeight(null), transparency);
            } catch (HeadlessException e) {
                // The system does not have a screen
            }
        
            if (bimage == null) {
                // Create a buffered image using the default color model
                int type = BufferedImage.TYPE_INT_RGB;
                bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
            }
        
            // Copy image to buffered image
            Graphics g = bimage.createGraphics();
        
            // Paint the image onto the buffered image
            g.drawImage(image, 0, 0, null);
            g.dispose();
        
            return bimage;
        }
        
    
    public static BufferedImage toTransparentBufferedImage(Image image) {
            if (image instanceof BufferedImage) {
                return (BufferedImage)image;
            }
        
            // This code ensures that all the pixels in the image are loaded
            image = new ImageIcon(image).getImage();
        
            // Create a buffered image with a format that's compatible with the screen
            BufferedImage bimage = null;
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            try {
                // Determine the type of transparency of the new buffered image
                int transparency = Transparency.TRANSLUCENT;
                // Create the buffered image
                GraphicsDevice gs = ge.getDefaultScreenDevice();
                GraphicsConfiguration gc = gs.getDefaultConfiguration();
                bimage = gc.createCompatibleImage(
                    image.getWidth(null), image.getHeight(null), transparency);
            } catch (HeadlessException e) {
                // The system does not have a screen
            }
        
            if (bimage == null) {
                // Create a buffered image using the default color model
                int type = BufferedImage.TYPE_INT_ARGB;
                bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
            }
        
            // Copy image to buffered image
            Graphics g = bimage.createGraphics();
        
            // Paint the image onto the buffered image
            g.drawImage(image, 0, 0, null);
            g.dispose();
        
            return bimage;
        }

    // This method returns true if the specified image has transparent pixels
       public static boolean hasAlpha(Image image) {
           // If buffered image, the color model is readily available
           if (image instanceof BufferedImage) {
               BufferedImage bimage = (BufferedImage)image;
               return bimage.getColorModel().hasAlpha();
           }
       
           // Use a pixel grabber to retrieve the image's color model;
           // grabbing a single pixel is usually sufficient
            PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
           try {
               pg.grabPixels();
           } catch (InterruptedException e) {
           }
       
           // Get the image's color model
           ColorModel cm = pg.getColorModel();
           return cm.hasAlpha();
       }

  public static void main(String args[]) throws Exception {
    System.out.println(alphanumericFormat("abi423423423"));
    
    
    /**System.out.println(emailFormat("abiagneya.com"));
    System.out.println(emailFormat("abi@agneyacom"));
    System.out.println(emailFormat("abi@agneya.in"));
    System.out.println(emailFormat("abi@in.in"));
    System.out.println(emailFormat("in@in.in"));
  
    System.out.println(Utils.getRoundedDollarCent(2.00));
    System.out.println(Utils.getRoundedDollarCent(.5));
    System.out.println(Utils.getRoundedDollarCent(1.5));

    byte[] bt = {
        '&', '#', '5', '4', '6', '2', '0', ';'};
    String hangul = new String(new String(
        "\u1112\u1161\u11ab\u1100\u1173\u11af").getBytes(), "EUC-KR");

    String str = new String(bt, "EUC-KR");

    System.out.println(str + hangul);

    double[] parts = Utils.integralDivide(0.25, 3);

    for (int i = 0; i < parts.length; i++) {
      System.out.println(parts[i]);
    }

    parts = Utils.integralDivide(0.02, 2);

    for (int i = 0; i < parts.length; i++) {
      System.out.println(parts[i]);
    }

    System.out.println(Utils.getRoundedString(2.39850934));

    //System.out.println(getCountry("202.153.40.47"));
    String input = "38.5455555";

    Locale loc = Locale.ITALY;
    int digits = 2;
    String formattedNumber = getFormattedNumber(loc, digits, input);
    String formattedCurrency = getFormattedCurrency(input);
    System.out.println(" formatted  currency is: " + formattedCurrency);
    System.out.println(" formatted  number   is: " + formattedNumber);
**/
    double d = 12344.43543454;
    System.out.println(" rounded: d=" + getRounded(d));
  }

}
