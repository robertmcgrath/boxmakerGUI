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

package org.cucfablab.boxmaker;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class splashScreen {

	String title = null;
	String path = null;

	public splashScreen(String tit, String filePath) {
		title = tit;
		path = filePath;
	}

	public void postIt() {
		Display display = Display.getDefault();
		Shell splash = new Shell(display);

		Label label = new Label(splash, SWT.NONE);
		label.setText(title);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		splash.setLayout(gridLayout);

		GridData data = new GridData();
		data.horizontalSpan = 3;

		Browser browser;
		try {
			browser = new Browser(splash, SWT.NONE);
		} catch (SWTError ee) {
			System.out.println("Could not instantiate Browser: "
					+ ee.getMessage());
			// display.dispose();
			return;
		}
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 3;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		browser.setLayoutData(data);
		File f = null;
		f = new File(path);
		browser.setUrl(f.toURI().toString());

		splash.open();
		while (!splash.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

}
