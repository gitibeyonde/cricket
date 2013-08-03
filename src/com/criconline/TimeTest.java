package com.criconline;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date;
import java.text.SimpleDateFormat;

public class TimeTest {
  public TimeTest() {
  }

  public static void main(String args[]){

    Calendar rightNow = Calendar.getInstance();
    rightNow.set(Calendar.SECOND, 0);
   rightNow.setTimeZone(TimeZone.getTimeZone("GMT-5:30"));
     System.out.println("Right now=" + rightNow.getTime().toString());

  }

}
