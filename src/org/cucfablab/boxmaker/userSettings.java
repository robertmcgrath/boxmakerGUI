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
 *  Collection of all the user inputs, optional sanity checks.
 */
package org.cucfablab.boxmaker;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;

public class userSettings {
	private double height, width, depth;
	private double cutwid, slotwid, slotdep;
	private boolean doPortrait = true;
	private boolean valuesSet = false;
	private String userSavedFile = null;
	private boolean useInches = false;

	static final float MM_PER_INCH = 25.4f;

	public void setUserSavedFile(String p) {
		userSavedFile = p;

	}

	public String getUserSavedFile() {
		return userSavedFile;
	}

	public void setPortrait(boolean p) {
		doPortrait = p;

	}

	public boolean getPortrait() {
		return doPortrait;
	}

	public void setHeight(double h) {
		height = h;
		valuesSet = true;
	}

	public double getHeight() {
		return height;
	}

	public double getHeight(boolean inches) {
		if (inches) {
			return (height * MM_PER_INCH);
		} else {
			return height;
		}
	}

	public void setWidth(double w) {
		width = w;
		valuesSet = true;
	}

	public double getWidth() {
		return width;
	}

	public double getWidth(boolean inches) {
		if (inches) {
			return (width * MM_PER_INCH);
		} else {
			return width;
		}
	}

	public void setDepth(double d) {
		depth = d;
		valuesSet = true;
	}

	public double getDepth() {
		return depth;
	}

	public double getDepth(boolean inches) {
		if (inches) {
			return (depth * MM_PER_INCH);
		} else {
			return depth;
		}
	}

	public void setSlotWidth(double sw) {
		slotwid = sw;
		valuesSet = true;
	}

	public double getSlotWidth() {
		return slotwid;
	}

	public double getSlotWidth(boolean inches) {
		if (inches) {
			return (slotwid * MM_PER_INCH);
		} else {
			return slotwid;
		}
	}

	public void setSlotDepth(double sd) {
		slotdep = sd;
		valuesSet = true;
	}

	public double getSlotDepth() {
		return slotdep;
	}

	public double getSlotDepth(boolean inches) {
		if (inches) {
			return (slotdep * MM_PER_INCH);
		} else {
			return slotdep;
		}
	}

	public void setCutWidth(double cw) {
		cutwid = cw;
		valuesSet = true;
	}

	public double getCutWidth() {
		return cutwid;
	}

	public double getCutWidth(boolean inches) {
		if (inches) {
			return (cutwid * MM_PER_INCH);
		} else {
			return cutwid;
		}
	}

	public void setUseInches(boolean b) {
		useInches = b;

	}

	public boolean getUseInches() {
		return useInches;
	}

	public boolean allReset() {
		return !valuesSet;
	}

	/**
	 * 
	 * Check the logical consistency and validity of the current settings in the
	 * userSettings.
	 * 
	 * Additional checks should be added here.
	 * 
	 * @param popup  generate a popup dialog with errors
	 * @return true if everything OK, false if error found
	 */
	public boolean testConstraints(boolean popup) {
		// check logical constraints on values
		boolean isOK = true;
		int numErrors = 0;
		String msg = "";
		
		// if any constraint fails, add string to the msg.
		
		if (height < 1.0) {
			msg += "height < 1: dimesions cannot be less than 1 mm\n";
			isOK = false;
			numErrors++;
		}
		if (width < 1.0) {
			msg += "width < 1: dimesions cannot be less than 1 mm\n";
			isOK = false;
			numErrors++;
		}
		if (depth < 1.0) {
			msg += "depth < 1: dimesions cannot be less than 1 mm\n";
			isOK = false;
			numErrors++;
		}

		if (height < (slotdep * 3)) {
			msg += "height too small: dimesions cannot be less than thickness\n";
			isOK = false;
			numErrors++;
		}
		if (depth < (slotdep * 3)) {
			msg += "depth too small: dimesions cannot be less than thickness\n";
			isOK = false;
			numErrors++;
		}
		if (width < (slotdep * 3)) {
			msg += "width too small: dimesions cannot be less than thickness\n";
			isOK = false;
			numErrors++;
		}
		if (slotwid > (height / 3)) {
			msg += "slotwidth too small: slots must be < 1/3 height\n";
			isOK = false;
			numErrors++;
		}
		if (slotwid > (width / 3)) {
			msg += "slotwidth too small: slots must be < 1/3 width\n";
			isOK = false;
			numErrors++;
		}
		if (slotwid > (depth / 3)) {
			msg += "slotwidth too small: slots must be < 1/3 depth\n";
			isOK = false;
			numErrors++;
		}
		if (slotwid < (slotdep + cutwid)) {
			msg += "slotwidth must be > slotdep + cutwid" + "\n";
			isOK = false;
			numErrors++;
		}

		if (cutwid > (slotwid / 10)) {
			msg += "cutwidth too large: must be < 1/10 slotwidth" + "\n";
			isOK = false;
			numErrors++;
		}

		// more error checks?

		if ((numErrors > 0) && popup) {
			// pop up an error dialog with the messages
			Status status = new Status(IStatus.ERROR, "BoxMakerGUI", 0,
					"Parameter Errors: Constraints violated", null);
			ErrorDialog.openError(Display.getCurrent().getActiveShell(),
					"BoxMaker Error", msg, status);
		}

		return isOK;
	}

	public void reset() {
		height = 0.0;
		width = 0.0;
		depth = 0.0;
		cutwid = 0.0;
		slotwid = 0.0;
		slotdep = 0.0;
		userSavedFile = null;
		useInches = false;
		valuesSet = false;
	}

};
