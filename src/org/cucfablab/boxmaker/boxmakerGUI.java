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
 *  
 *  A GUI front end for the 'boxmaker' program from :  https://github.com/rahulbot/boxmaker
 *  
 *  Created for the Champaign Urbana Community Fab Lab (http://cucfablag.org)
 *  
 */

package org.cucfablab.boxmaker;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Scanner;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.icepdf.ri.util.PropertiesManager;
import org.icepdf.ri.viewer.Launcher;

public class boxmakerGUI {

	protected Shell shlBoxmaker;
	private Text enterHeight;
	private Text enterWidth;
	private Text enterDepth;
	private Text enterSlotWidth;
	private Text enterSlotDepth;
	private Text enterCutWidth;
	private Button btnMakeBox;
	private Button btnReset;
	private Button btnPreview;
	private Button btnPortrait;
	private Button btnRadioButtonUnitsMM;
	private Button btnRadioButtonUnitsInches;
	private userSettings us;
	private myVerifyListener mvl;
	private List materialsList;
	private materialsList materials = new materialsList();

	mySelectionAdapter msa;

	public boxmakerGUI() {
		us = new userSettings();
		mvl = new myVerifyListener();
		mvl.setUS(us);
		msa = new mySelectionAdapter();
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			boxmakerGUI window = new boxmakerGUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlBoxmaker.open();
		shlBoxmaker.layout();
		while (!shlBoxmaker.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * 
	 * Private class that handles the selection events for the GUI.
	 * 
	 * Sets and gets values from the global userSettings object
	 * 
	 * Handles the behavior of the buttons, checkbox, and list.
	 *
	 */
	// this is a private class because it messes with the values in the display.
	private class mySelectionAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (us == null) { // error
				System.err.println("us == null?");
			}

			if (e.getSource().getClass().getName().contains("Button")) {

				Button b = (Button) e.getSource();

				if (b.getToolTipText().contains("Reset")) {
					// reset the state, and clear all the displayed fields
					us.reset();
					enterHeight.setText("0.0");
					enterWidth.setText("0.0");
					enterDepth.setText("0.0");
					enterSlotWidth.setText("0.0");
					enterSlotDepth.setText("0.0");
					enterCutWidth.setText("0.0");
					btnPortrait.setSelection(false);
					materialsList.setSelection(0);
					btnPreview.setEnabled(false);
					btnRadioButtonUnitsMM.setSelection(true);
					btnRadioButtonUnitsInches.setSelection(false);
				} else if (b.getToolTipText().contains("Quit")) {
					System.exit(0);
				} else if (b.getToolTipText().contains("Explain")) {
					// create a separate splash screen and display the user instructions
					splashScreen s1 = new splashScreen("Box Maker Parameters",
							"docs/BoxParms1.htm");
					s1.postIt();
				} else if (b.getToolTipText().contains("Help")) {
					// create a separate splash screen and display the general informations
					splashScreen s1 = new splashScreen("About BoxMaker",
							"docs/Information_about_Boxmaker.htm");
					s1.postIt();
				} else if (b.getToolTipText().contains("Make")) {
					// check the parameters, then call rahulbot BoxMaker
					//
					//  this is the main action to create the box
					//
					boolean isOK = us.testConstraints(true);
					// if 'not isOK' then the testConstraints will have displayed a dialog
					//    to do:  check behavior when !isOK
					if (isOK) {
						File bf = new File("BOX.jar");
						if (!bf.exists()) {
							// fail here because the required program is not found
							Status status = new Status(IStatus.ERROR,
									"BoxMakerGUI", 0,
									"BoxMaker Program Exec Failed", null);
							ErrorDialog.openError(Display.getCurrent()
									.getActiveShell(), "BOX.jar not found",
									"BoxMaker Failed", status);

							return;
						}

						// retrieve the parameters from the userSEttings, construct the call to
						//  BOX.jar
						String cmd = "java -cp BOX.jar com.rahulbotics.boxmaker.CommandLine "
								+ "boxmaker.pdf "
								+ us.getWidth(us.getUseInches())
								+ " "
								+ us.getHeight(us.getUseInches())
								+ " "
								+ us.getDepth(us.getUseInches())
								+ " "
								+ us.getSlotDepth(us.getUseInches())
								+ " "
								+ us.getCutWidth(us.getUseInches())
								+ " "
								+ us.getSlotWidth(us.getUseInches())
								+ " "
								+ "false true true true " + us.getPortrait();
						boolean failed = false;
						//System.out.println("exec " + cmd);
						// Execute the command n a separate process
						try {
							Process p = Runtime.getRuntime().exec(cmd);
							
							// print the output of the process, if any, to the console (mainly for debugging)
							String scan = "";
							Scanner sc = new Scanner(p.getInputStream());
							int lns = 0;
							while (sc.hasNext()) {
								System.out.println(sc.nextLine());
								scan += sc.nextLine();
								scan += "\n";
								lns++;
							}
							sc.close();
							if (lns > 0) {
								// if there is any output, there was an error, so pop up a dialog
								Status status = new Status(IStatus.INFO,
										"BoxMakerGUI", 0,
										"BoxMaker Program Messages", null);
								ErrorDialog.openError(Display.getCurrent()
										.getActiveShell(), "BoxMaker Messages",
										scan, status);
							}
							if (p.exitValue() == 0) {
								// job failed for some reason
								failed = true;
							}
						} catch (IOException ioe) {
							// the fork/exec raised an exception, job failed.  Pop up a dialog with the exception.
							Status status = new Status(IStatus.ERROR,
									"BoxMakerGUI", 0,
									"BoxMaker Program Exec Failed", null);
							ErrorDialog.openError(Display.getCurrent()
									.getActiveShell(), "BoxMaker Failed", ioe
									.getMessage(), status);
							failed = true;
						}
						
						// Process completed, result should be in file called 'boxmaker.pdf' 
						File f = new File("boxmaker.pdf");
						if (!f.exists()) {
							// No output was created, job failed.
							// to do probably should raise a dialog here...
							System.err.println("output file not found...");
							failed = true;

						}
						if (!failed) {
							// Success!  
							// Save the output to a file selected by the user,
							// pop up a 'safe save dialog', rename the default file
							Display display = Display.getDefault();
							Shell shell = new Shell(display);
							mySafeSaveDialog ssfile = new mySafeSaveDialog(
									shell);

							ssfile.open();

							String extra = ".pdf";
							if (ssfile.getFileName().endsWith(".pdf")) {
								extra = "";
							}
							//System.out.println("save " + f.getAbsolutePath()
							//		+ "as " + ssfile.getFileName() + " in "
							//		+ ssfile.getFilterPath());

							f.renameTo(new File(ssfile.getFilterPath() + "/"
									+ ssfile.getFileName() + extra));
							
							// note:  remember the file name so we can find the most recent result...
							us.setUserSavedFile(ssfile.getFilterPath() + "/"
									+ ssfile.getFileName() + extra);
							
							// enable the preview button now that there is something to preview
							btnPreview.setEnabled(true);
							while (!shell.isDisposed()) {
								if (!display.readAndDispatch())
									display.sleep();
							}
							display.dispose();
						}

					} 
					// note:  'not OK/ comes here-- messages already shown.

				} else if (b.getToolTipText().contains("Preview")) {

					// Preview the most recently created PDF
					//
					// This uses the a class that uses icePDF.
					Launcher l = new Launcher();

					String contentURL = "";
					String contentFile = us.getUserSavedFile();
					String contentProperties = null;

					// load message bundle
					ResourceBundle messageBundle = ResourceBundle
							.getBundle(PropertiesManager.DEFAULT_MESSAGE_BUNDLE);
					l.run(contentFile, contentURL, contentProperties,
							messageBundle);

				} else if (b.getToolTipText().contains("Units")) {
					// Select the units.  Numbers entered will be converted to millimeters if needed
					if (btnRadioButtonUnitsMM.getSelection()) {
						us.setUseInches(false);
					} else {
						us.setUseInches(true);
					}
				}

			} else if (e.getSource().getClass().getName().contains("Check")) {
				//  The checkbox selects portrait/landscape layout
				if (us.getPortrait()) {
					us.setPortrait(false);
					btnPortrait.setSelection(false);
				} else {
					us.setPortrait(true);
					btnPortrait.setSelection(false);
				}
			} else if (e.getSource().getClass().getName().contains("List")) {
				// Selection from the list of materials
				//  look up the stored cut width
				List l = (List) (e.getSource());
				String[] sel = l.getSelection();
				if (sel.length > 0) {
					if (sel.length > 1) {
						// ??
					}

					double value = 0.0;

					value = materials.getWidth(sel[0], us.getUseInches());

					us.setCutWidth(value);

					// / WARNING -- THIS IS RESETTING THE VISIBLE VALUE ON THE
					// SCREEN...
					enterCutWidth.setText(new Double(value).toString());

				}

			}

		}

		/**
		 * This class provides a facade for the "save" FileDialog class. If the
		 * selected file already exists, the user is asked to confirm before
		 * overwriting.
		 */

		private class mySafeSaveDialog {
			// The wrapped FileDialog
			private FileDialog dlg;

			/**
			 * SafeSaveDialog constructor
			 * 
			 * @param shell
			 *            the parent shell
			 */

			public mySafeSaveDialog(Shell shell) {
				dlg = new FileDialog(shell, SWT.SAVE);
			}

			public String open() {
				// We store the selected file name in fileName
				String fileName = null;

				// The user has finished when one of the
				// following happens:
				// 1) The user dismisses the dialog by pressing Cancel
				// 2) The selected file name does not exist
				// 3) The user agrees to overwrite existing file
				boolean done = false;

				while (!done) {
					// Open the File Dialog
					fileName = dlg.open();
					if (fileName == null) {
						// User has cancelled, so quit and return
						done = true;
					} else {
						// User has selected a file; see if it already exists
						File file = new File(fileName);
						if (file.exists()) {
							// The file already exists; asks for confirmation
							MessageBox mb = new MessageBox(dlg.getParent(),
									SWT.ICON_WARNING | SWT.YES | SWT.NO);

							// We really should read this string from a
							// resource bundle
							mb.setMessage(fileName
									+ " already exists. Do you want to replace it?");

							// If they click Yes, we're done and we drop out. If
							// they click No, we redisplay the File Dialog
							done = mb.open() == SWT.YES;
						} else {
							// File does not exist, so drop out
							done = true;
						}
					}
				}
				return fileName;
			}

			public String getFileName() {
				return dlg.getFileName();
			}

			@SuppressWarnings("unused")
			public String[] getFileNames() {
				return dlg.getFileNames();
			}

			@SuppressWarnings("unused")
			public String[] getFilterExtensions() {
				return dlg.getFilterExtensions();
			}

			@SuppressWarnings("unused")
			public String[] getFilterNames() {
				return dlg.getFilterNames();
			}

			public String getFilterPath() {
				return dlg.getFilterPath();
			}

			@SuppressWarnings("unused")
			public void setFileName(String string) {
				dlg.setFileName(string);
			}

			@SuppressWarnings("unused")
			public void setFilterExtensions(String[] extensions) {
				dlg.setFilterExtensions(extensions);
			}

			@SuppressWarnings("unused")
			public void setFilterNames(String[] names) {
				dlg.setFilterNames(names);
			}

			@SuppressWarnings("unused")
			public void setFilterPath(String string) {
				dlg.setFilterPath(string);
			}

			@SuppressWarnings("unused")
			public Shell getParent() {
				return dlg.getParent();
			}

			@SuppressWarnings("unused")
			public int getStyle() {
				return dlg.getStyle();
			}

			@SuppressWarnings("unused")
			public String getText() {
				return dlg.getText();
			}

			@SuppressWarnings("unused")
			public void setText(String string) {
				dlg.setText(string);
			}
		}
	}

	/**
	 * Create contents of the window. (Generated by SWT editor.)
	 */
	protected void createContents() {

		shlBoxmaker = new Shell();
		shlBoxmaker.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shlBoxmaker.setText("BoxMaker");
		GridLayout gl_shlBoxmaker = new GridLayout(7, false);
		gl_shlBoxmaker.marginTop = 5;
		gl_shlBoxmaker.marginRight = 5;
		gl_shlBoxmaker.marginLeft = 5;
		shlBoxmaker.setLayout(gl_shlBoxmaker);

		Label lblChampaignUrbanaComunity = new Label(shlBoxmaker, SWT.NONE);
		lblChampaignUrbanaComunity.setLayoutData(new GridData(SWT.CENTER,
				SWT.CENTER, false, false, 7, 1));
		lblChampaignUrbanaComunity.setText("Champaign Urbana Comunity Fab Lab");
		lblChampaignUrbanaComunity.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_GREEN));
		lblChampaignUrbanaComunity.setFont(SWTResourceManager.getFont("Arial",
				24, SWT.BOLD));

		Label lblBoxmakerfrom = new Label(shlBoxmaker, SWT.NONE);
		lblBoxmakerfrom.setFont(SWTResourceManager.getFont("Arial", 14,
				SWT.BOLD));
		lblBoxmakerfrom.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				false, false, 7, 1));
		lblBoxmakerfrom
				.setText("BoxMaker Program (adapted from rahulbotics.com/)");

		Group unitSelection = new Group(shlBoxmaker, SWT.NONE);
		unitSelection.setFont(SWTResourceManager.getFont("Arial", 12,
				SWT.NORMAL));
		GridData gd_group = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2,
				1);

		gd_group.widthHint = 121;
		unitSelection.setLayoutData(gd_group);
		unitSelection.setLayout(new RowLayout(SWT.HORIZONTAL));

		unitSelection.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));

		btnRadioButtonUnitsMM = new Button(unitSelection, SWT.RADIO);
		btnRadioButtonUnitsMM.setText("mm");
		btnRadioButtonUnitsMM.setFont(SWTResourceManager.getFont("Arial", 14,
				SWT.NORMAL));
		btnRadioButtonUnitsMM.setToolTipText("UnitsMM");
		btnRadioButtonUnitsMM.setSelection(true);
		btnRadioButtonUnitsMM.addSelectionListener(msa);

		btnRadioButtonUnitsInches = new Button(unitSelection, SWT.RADIO
				| SWT.RIGHT);
		btnRadioButtonUnitsInches.setAlignment(SWT.LEFT);
		btnRadioButtonUnitsInches.setFont(SWTResourceManager.getFont("Arial",
				14, SWT.NORMAL));
		btnRadioButtonUnitsInches.setText("inches");
		btnRadioButtonUnitsInches.setToolTipText("UnitsIn");
		btnRadioButtonUnitsInches.addSelectionListener(msa);

		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);

		Button btnAbout = new Button(shlBoxmaker, SWT.NONE);
		btnAbout.setFont(SWTResourceManager.getFont("Arial", 14, SWT.NORMAL));
		btnAbout.setToolTipText("Help");
		btnAbout.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		btnAbout.setText("About");
		btnAbout.addSelectionListener(msa);

		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);

		Label lblHeightmm = new Label(shlBoxmaker, SWT.NONE);
		lblHeightmm
				.setFont(SWTResourceManager.getFont("Arial", 14, SWT.NORMAL));
		lblHeightmm.setImage(null);
		lblHeightmm.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblHeightmm.setText("Height");
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);

		enterHeight = new Text(shlBoxmaker, SWT.BORDER | SWT.RIGHT);
		enterHeight
				.setFont(SWTResourceManager.getFont("Arial", 14, SWT.NORMAL));
		GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
				1);
		gd_text.widthHint = 100;
		enterHeight.setLayoutData(gd_text);
		enterHeight.setText("0.0");
		enterHeight.setToolTipText("Height");
		enterHeight.addVerifyListener(mvl);

		Button btnExplain = new Button(shlBoxmaker, SWT.NONE);
		btnExplain.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		btnExplain.setFont(SWTResourceManager.getFont("Arial", 14, SWT.NORMAL));
		btnExplain.setToolTipText("Explain");
		btnExplain.setText("Explain");
		btnExplain.addSelectionListener(msa);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);

		Label lblWidthmm = new Label(shlBoxmaker, SWT.NONE);
		lblWidthmm.setFont(SWTResourceManager.getFont("Arial", 14, SWT.NORMAL));
		lblWidthmm.setToolTipText("Width");
		lblWidthmm.setText("Width");
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);

		enterWidth = new Text(shlBoxmaker, SWT.BORDER | SWT.RIGHT);
		enterWidth.setFont(SWTResourceManager.getFont("Arial", 14, SWT.NORMAL));
		GridData gd_text_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false,
				1, 1);
		gd_text_1.widthHint = 100;
		enterWidth.setLayoutData(gd_text_1);
		enterWidth.setText("0.0");
		enterWidth.setToolTipText("Width");
		enterWidth.addVerifyListener(mvl);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);

		Label lblDepthmm = new Label(shlBoxmaker, SWT.NONE);
		lblDepthmm.setFont(SWTResourceManager.getFont("Arial", 14, SWT.NORMAL));
		lblDepthmm.setText("Depth");
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);

		enterDepth = new Text(shlBoxmaker, SWT.BORDER | SWT.RIGHT);
		enterDepth.setFont(SWTResourceManager.getFont("Arial", 14, SWT.NORMAL));
		GridData gd_text_2 = new GridData(SWT.LEFT, SWT.CENTER, false, false,
				1, 1);
		gd_text_2.widthHint = 100;
		enterDepth.setLayoutData(gd_text_2);
		enterDepth.setText("0.0");
		enterDepth.setToolTipText("Depth");
		enterDepth.addVerifyListener(mvl);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);

		new Label(shlBoxmaker, SWT.SEPARATOR | SWT.HORIZONTAL);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);

		Label lblSlotWidthhmm = new Label(shlBoxmaker, SWT.NONE);
		lblSlotWidthhmm.setFont(SWTResourceManager.getFont("Arial", 14,
				SWT.NORMAL));
		lblSlotWidthhmm.setText("Slot Width");
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);

		enterSlotWidth = new Text(shlBoxmaker, SWT.BORDER | SWT.RIGHT);
		enterSlotWidth.setFont(SWTResourceManager.getFont("Arial", 14,
				SWT.NORMAL));
		GridData gd_text_3 = new GridData(SWT.LEFT, SWT.CENTER, false, false,
				1, 1);
		gd_text_3.widthHint = 100;
		enterSlotWidth.setLayoutData(gd_text_3);
		enterSlotWidth.setText("0.0");
		enterSlotWidth.setToolTipText("SlotWidth");
		enterSlotWidth.addVerifyListener(mvl);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);

		Label lblSlotDepthhmm = new Label(shlBoxmaker, SWT.NONE);
		lblSlotDepthhmm.setFont(SWTResourceManager.getFont("Arial", 14,
				SWT.NORMAL));
		lblSlotDepthhmm.setText("Slot Depth (thickness)");
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);

		enterSlotDepth = new Text(shlBoxmaker, SWT.BORDER | SWT.RIGHT);
		enterSlotDepth.setFont(SWTResourceManager.getFont("Arial", 14,
				SWT.NORMAL));
		GridData gd_text_4 = new GridData(SWT.LEFT, SWT.CENTER, false, false,
				1, 1);
		gd_text_4.widthHint = 100;
		enterSlotDepth.setLayoutData(gd_text_4);
		enterSlotDepth.setText("0.0");
		enterSlotDepth.setToolTipText("SlotDepth");
		enterSlotDepth.addVerifyListener(mvl);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);

		new Label(shlBoxmaker, SWT.SEPARATOR | SWT.HORIZONTAL);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);

		Label lblSCutWidthhmm = new Label(shlBoxmaker, SWT.NONE);
		lblSCutWidthhmm.setFont(SWTResourceManager.getFont("Arial", 14,
				SWT.NORMAL));
		lblSCutWidthhmm.setText("Cut Width");
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);

		enterCutWidth = new Text(shlBoxmaker, SWT.BORDER | SWT.RIGHT);
		enterCutWidth.setFont(SWTResourceManager.getFont("Arial", 14,
				SWT.NORMAL));
		GridData gd_text_5 = new GridData(SWT.LEFT, SWT.CENTER, false, false,
				1, 1);
		gd_text_5.widthHint = 100;
		enterCutWidth.setLayoutData(gd_text_5);
		enterCutWidth.setText("0.0");
		enterCutWidth.setToolTipText("CutWidth");
		enterCutWidth.addVerifyListener(mvl);

		materialsList = new List(shlBoxmaker, SWT.BORDER | SWT.V_SCROLL);
		materialsList.setFont(SWTResourceManager.getFont("Arial", 14,
				SWT.NORMAL));
		GridData gd_list = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
				1);
		gd_list.widthHint = 136;
		gd_list.heightHint = 40;
		materialsList.setLayoutData(gd_list);
		materialsList.setToolTipText("MaterialList");
		materialsList.setItems(materials.getList());
		materialsList.setSelection(0);
		materialsList.addSelectionListener(msa);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);

		new Label(shlBoxmaker, SWT.SEPARATOR | SWT.HORIZONTAL);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);

		btnPortrait = new Button(shlBoxmaker, SWT.CHECK);
		btnPortrait
				.setFont(SWTResourceManager.getFont("Arial", 14, SWT.NORMAL));
		btnPortrait.setText("Portrait");
		btnPortrait.setToolTipText("ToggleLandsape");
		btnPortrait.addSelectionListener(msa);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);

		btnReset = new Button(shlBoxmaker, SWT.NONE);
		btnReset.setFont(SWTResourceManager.getFont("Arial", 14, SWT.NORMAL));
		btnReset.setToolTipText("Reset");
		btnReset.setText("Reset");
		btnReset.addSelectionListener(msa);

		btnMakeBox = new Button(shlBoxmaker, SWT.NONE);
		btnMakeBox.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		btnMakeBox.setFont(SWTResourceManager.getFont("Arial", 14, SWT.NORMAL));
		btnMakeBox.setToolTipText("Make Box");
		btnMakeBox.setText("Make Box");
		btnMakeBox.addSelectionListener(msa);
		new Label(shlBoxmaker, SWT.NONE);

		btnPreview = new Button(shlBoxmaker, SWT.NONE);
		btnPreview.setEnabled(false);
		btnPreview.setFont(SWTResourceManager.getFont("Arial", 14, SWT.NORMAL));
		btnPreview.setToolTipText("Preview");
		btnPreview.setText("Preview");
		btnPreview.addSelectionListener(msa);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);

		Button btnQuit = new Button(shlBoxmaker, SWT.NONE);
		btnQuit.setFont(SWTResourceManager.getFont("Arial", 14, SWT.NORMAL));
		btnQuit.setToolTipText("Quit");
		btnQuit.setText("Quit");
		btnQuit.addSelectionListener(msa);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);
		new Label(shlBoxmaker, SWT.NONE);

	}
}
