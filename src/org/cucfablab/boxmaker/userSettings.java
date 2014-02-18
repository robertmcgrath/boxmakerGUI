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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.xml.sax.*;
import org.w3c.dom.*;

public class userSettings {
	private double height, width, depth;
	private double cutwid, slotwid, slotdep;
	private boolean doPortrait = false;
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
	 * @param popup
	 *            generate a popup dialog with errors
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
		doPortrait = false;
		userSavedFile = null;
		useInches = false;
		valuesSet = false;
	}

	// // not working yet crashes when transform is called....
	public void saveToFile(String filename) {
		// tryit();
		Document dom;
		Element e = null;

		// instance of a DocumentBuilderFactory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			// use factory to get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// create instance of DOM
			dom = db.newDocument();

			// create the root element
			Element rootEle = dom.createElement("userSettings");
			dom.appendChild(rootEle);

			// create data elements and place them under root
			e = dom.createElement("height");
			e.appendChild(dom.createTextNode(Double.toString(height)));
			rootEle.appendChild(e);

			e = dom.createElement("width");
			e.appendChild(dom.createTextNode(Double.toString(width)));
			rootEle.appendChild(e);

			e = dom.createElement("depth");
			e.appendChild(dom.createTextNode(Double.toString(depth)));
			rootEle.appendChild(e);

			e = dom.createElement("cutwid");
			e.appendChild(dom.createTextNode(Double.toString(cutwid)));
			rootEle.appendChild(e);

			e = dom.createElement("slotwid");
			e.appendChild(dom.createTextNode(Double.toString(slotwid)));
			rootEle.appendChild(e);

			e = dom.createElement("slotdep");
			e.appendChild(dom.createTextNode(Double.toString(slotdep)));
			rootEle.appendChild(e);

			e = dom.createElement("doPortrait");
			e.appendChild(dom.createTextNode(Boolean.toString(doPortrait)));
			rootEle.appendChild(e);

			if (userSavedFile == null) {
				e = dom.createElement("userSavedFile");
				e.appendChild(dom.createTextNode("null"));
				rootEle.appendChild(e);
			} else {
				e = dom.createElement("userSavedFile");
				e.appendChild(dom.createTextNode(userSavedFile));
				rootEle.appendChild(e);
			}

			e = dom.createElement("useInches");
			e.appendChild(dom.createTextNode(Boolean.toString(useInches)));
			rootEle.appendChild(e);

			try {

				Transformer tr = TransformerFactory.newInstance()
						.newTransformer();
				tr.setOutputProperty(OutputKeys.INDENT, "yes");
				tr.setOutputProperty(OutputKeys.METHOD, "xml");
				tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				// tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
				// "userSettings.dtd");
				tr.setOutputProperty(
						"{http://xml.apache.org/xslt}indent-amount", "4");

				StreamResult sr = new StreamResult(new File(filename));
				DOMSource ds = new DOMSource(dom);

				// send DOM to file
				tr.transform(ds, sr);
				// System.out.println("written to "+filename);
			} catch (TransformerException te) {
				System.out.println(te.getMessage());
			}
		} catch (ParserConfigurationException pce) {
			System.out
					.println("UsersXML: Error trying to instantiate DocumentBuilder "
							+ pce);
		}
	}

	private void setValueFromText(String name, String value) {
		if (name.equals("height")) {
			Double v = new Double(value);
			height = v;
		} else if (name.equals("width")) {
			Double v = new Double(value);
			width = v;
		} else if (name.equals("depth")) {
			Double v = new Double(value);
			depth = v;
		} else if (name.equals("depth")) {
			Double v = new Double(value);
			depth = v;
		} else if (name.equals("depth")) {
			Double v = new Double(value);
			depth = v;
		} else if (name.equals("cutwid")) {
			Double v = new Double(value);
			cutwid = v;
		} else if (name.equals("slotwid")) {
			Double v = new Double(value);
			slotwid = v;
		} else if (name.equals("slotdep")) {
			Double v = new Double(value);
			slotdep = v;
		} else if (name.equals("doPortrait")) {
			if (value.equals("true")) {
				doPortrait = true;
			} else if (value.equals("false")) {
				doPortrait = false;
			} else {
				// ??? error???
			}

		} else if (name.equals("useInches")) {
			if (value.equals("true")) {
				useInches = true;
			} else if (value.equals("false")) {
				useInches = false;
			} else {
				// ??? error???
			}

		} else if (name.equals("userSavedFile")) {
			if (value.equals("null")) {
				userSavedFile = null;
			} else {
				userSavedFile = value;
			}

		}
	}

	private void parseElement(Document document, String targ) {
		NodeList nlist = document.getElementsByTagName(targ);

		if (nlist.getLength() > 0) {

			Node e = nlist.item(0);
			String n = e.getNodeName();
			NodeList hec = e.getChildNodes();

			if (hec.getLength() > 0) {
				Node hev = hec.item(0);
				String v = hev.getNodeValue();
				// System.out.println("setValue( "+n+", "+v);
				setValueFromText(n, v);
			}

		}
	}

	public void loadSettings(String xmlfile) {
		System.out.println("load from " + xmlfile);
		Document document = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			File f = new File(xmlfile);
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(f);

		} catch (SAXParseException spe) {
			// Error generated by the parser
			System.out.println("\n** Parsing error" + ", line "
					+ spe.getLineNumber() + ", uri " + spe.getSystemId());
			System.out.println("  " + spe.getMessage());

			// Use the contained exception, if any
			Exception x = spe;
			if (spe.getException() != null)
				x = spe.getException();
			x.printStackTrace();
		} catch (SAXException sxe) {
			// Error generated by this application
			// (or a parser-initialization error)
			Exception x = sxe;
			if (sxe.getException() != null)
				x = sxe.getException();
			x.printStackTrace();
		} catch (ParserConfigurationException pce) {
			// Parser with specified options
			// cannot be built
			pce.printStackTrace();
		} catch (IOException ioe) {
			// I/O error
			ioe.printStackTrace();
		}

		if (document != null) {

			parseElement(document, "height");
			parseElement(document, "width");
			parseElement(document, "depth");
			parseElement(document, "cutwid");
			parseElement(document, "slotwid");
			parseElement(document, "slotdep");
			parseElement(document, "doPortrait");
			parseElement(document, "userSavedFile");
			parseElement(document, "useInches");
			valuesSet = true;

		}
	}
};
