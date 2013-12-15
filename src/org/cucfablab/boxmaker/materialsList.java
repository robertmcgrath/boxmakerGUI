/**
 * 
 *  Copyright (c) 2013 Robert E. McGrath
 *
 *  Created for use by the Champaign Urbana Community Fab Lab
 *
 *  boxmakerGUI by Robert E. McGrath is licensed under a 
 *  Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 *  http://creativecommons.org/licenses/by-nc-sa/4.0/
 */

/*
 * Wish list:  read this data from XML, make it easy to reconfigure....
 */

package org.cucfablab.boxmaker;

import java.util.Vector;

public class materialsList {

	// list of materials and suggested cut widths...
	Vector<String> mats = new Vector<String>();
	Vector<Double> wids = new Vector<Double>();
	Vector<Double> widsInch = new Vector<Double>();

	public materialsList() {

		// The order here is the order they appear in the menu...
		mats.add("Custom");
		wids.add(0.0);
		widsInch.add(0.0);
		mats.add("1/4 in. Acryllic");
		wids.add(0.1);
		widsInch.add(0.003937008);
		mats.add("1/8 in. Acryllic");
		wids.add(0.25);
		widsInch.add(0.00984252);
		mats.add("1/8 in. Wood");
		wids.add(0.2);
		widsInch.add(0.007874016);

	}

	public String[] getList() {
		String[] theM = new String[mats.size()];
		for (int i = 0; i < mats.size(); i++) {
			theM[i] = mats.get(i);
		}
		return theM;
	}

	public double getWidth(String sel, boolean inches) {
		int ind = mats.indexOf(sel);
		if (ind < 0) {
			System.err.println("selection " + sel + " not found");
		} else {
			if (inches) {
				return widsInch.get(ind);
			} else {
				return wids.get(ind);
			}
		}

		return 0.0;
	}

}
