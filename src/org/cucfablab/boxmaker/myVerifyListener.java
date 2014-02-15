/**
 * 
 *  Copyright (c) 2013, 2014 Robert E. McGrath
 *
 *  Created for use by the Champaign Urbana Community Fab Lab
 *
 *  boxmakerGUI by Robert E. McGrath is licensed under a 
 *  Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 *  http://creativecommons.org/licenses/by-nc-sa/4.0/
 */

/*
 *  Listaner that manages input as float numbers.
 *  
 *  Input is saved in an instance of a 'userSettings' object, for later use.
 *  
 */
package org.cucfablab.boxmaker;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

public class myVerifyListener implements VerifyListener {

	private userSettings us;
	private boolean busy = false;

	public void setUS(userSettings u) {
		us = u;
	}

	@Override
	public void verifyText(VerifyEvent e) {

		// 'busy' flag can be used to prevent recursion if needed.  This happens if
		//  any of the code touches an input field.  Generally, this should not be done, but
		//  better safe than sorry.
		if (busy) {
			e.doit = false;
			return;
		}
		busy = true;

		Text text = (Text) e.getSource();

		// get old text and create new text by using the VerifyEvent.text
		final String oldS = text.getText();
		String newS = oldS.substring(0, e.start) + e.text
				+ oldS.substring(e.end);

		Double fv = null;

		boolean isFloat = true;
		try {
			fv = Double.parseDouble(newS);
		} catch (NumberFormatException ex) {
			isFloat = false;
		}
		
		if (fv == null) {
			// Erased whole field - OK, but set value to zero, leave field blank.
			fv = 0.0;
		}

		boolean isNum = false;

		// Not sure about this logic --
		// this is the case where there is one character, which does not parse
		// to a float--want to erase it?
		if (!isFloat && (oldS.length() == 1)) {
			isNum = true;
		}

		if ((!isFloat && !isNum) || (fv < 0.0)) {
			e.doit = false;
			// System.out.println("rejecting "+newS);
		}
		if (isFloat) {
			// set the appropriate parameter
			if (text.getToolTipText().startsWith("Height")) {
				us.setHeight(fv);
			}
			if (text.getToolTipText().startsWith("Width")) {
				us.setWidth(fv);
			}
			if (text.getToolTipText().startsWith("Depth")) {
				us.setDepth(fv);
			}
			if (text.getToolTipText().startsWith("CutWidth")) {
				us.setCutWidth(fv);
			}
			if (text.getToolTipText().startsWith("SlotWidth")) {
				us.setSlotWidth(fv);
			}
			if (text.getToolTipText().startsWith("SlotDepth")) {
				us.setSlotDepth(fv);
			}

		}
		busy = false;
	};

}
