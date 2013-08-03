package com.criconline.resources;

import java.util.Locale;
import java.util.ResourceBundle;

public class Bundle {

	private static ResourceBundle bundle;

	public static ResourceBundle getBundle() {
		if (bundle == null)
			bundle = ResourceBundle.getBundle(
				new Bundle().getClass().getPackage().getName() + ".messages",
				new Locale("en", "US"));
		return bundle;
	}

}
