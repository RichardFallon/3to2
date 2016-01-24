/*
3to2 is free software; you can redistribute it and/or modify
it under the terms of the Apache License version 2.

3to2 is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
 */
/** @author Richard Fallon
 * 3to2 Allows Conversion of Conceptual Graphs (in CGIF file format) into Formal Concepts (in Burmeister file format) and back again for round-trip engineering.
 **/
package Convertor;


import org.apache.log4j.BasicConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.tudresden.inf.tcs.fcaapi.exception.IllegalAttributeException;
import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import de.tudresden.inf.tcs.fcaapi.utils.IndexedSet;
import de.tudresden.inf.tcs.fcalib.FormalContext;
import de.tudresden.inf.tcs.fcalib.FullObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.fallon.cg.CGIFFile;
import com.fallon.cg.ConceptNode;
import com.fallon.cg.RelationNode;
import com.fallon.cg.Tripple;
import com.fallon.fca.BurmeisterFile;


public class CG_FCA_Convertor {

	private static final Logger logger = LogManager.getLogger("FCA_CG_Convertor");
	Display d;
	Shell s;

	public CG_FCA_Convertor() {

		super();

		//Initialize the logger
		BasicConfigurator.configure();

		d = new Display();
		s = new Shell(d);
		s.setSize(400, 400);

		s.setText("FCA/CG CG/FCA Convertor");
		//         create the menu system
		Menu m = new Menu(s, SWT.BAR);
		// create a file menu and add an exit item
		final MenuItem file = new MenuItem(m, SWT.CASCADE);
		file.setText("&Select Convertor");
		final Menu filemenu = new Menu(s, SWT.DROP_DOWN);
		file.setMenu(filemenu);
		final MenuItem openItem = new MenuItem(filemenu, SWT.PUSH);
		openItem.setText("&CG to FCA\tCTRL+O");
		openItem.setAccelerator(SWT.CTRL + 'O');
		final MenuItem saveItem = new MenuItem(filemenu, SWT.PUSH);
		saveItem.setText("&FCA to CG\tCTRL+S");
		saveItem.setAccelerator(SWT.CTRL + 'S');
		final MenuItem separator = new MenuItem(filemenu, SWT.SEPARATOR);
		final MenuItem exitItem = new MenuItem(filemenu, SWT.PUSH);
		exitItem.setText("E&xit");
		


		class Open implements SelectionListener {
			public void widgetSelected(SelectionEvent event) {
				FileDialog fd = new FileDialog(s, SWT.OPEN);
				fd.setText("CG to FCA");
				fd.setFilterPath("./");//"C:/Temp");
				String[] filterExt = { "*.cgif"};//txt", "*.doc", ".rtf", "*.*" };
				fd.setFilterExtensions(filterExt);
				String selected = fd.open();
				//System.out.println(selected);
				CGIFtoFCA(selected);
				dialog("CG to FCA conversion complete",s);
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}

		}


		// Define a static logger variable so that it references the
		// Logger instance named "MyApp".
		/*this main can be used instead of jface for testing
		public static void main(String[] args) {
			// Set up a simple configuration that logs on the console.
			logger.trace("Entering application.");
			//		logger.error("Didn't do it.");
			//		logger.trace("Exiting application.");   

			//Initialize the logger
			BasicConfigurator.configure();

			FCAtoCGIF("c:\\temp\\test.fca");

			// do a CGIF to FCA
			//CGIFtoFCA("c:\\temp\\P-H_University.cgif");

		}
		 */

		class Save implements SelectionListener {
			public void widgetSelected(SelectionEvent event) {
				FileDialog fd = new FileDialog(s, SWT.SAVE);
				fd.setText("FCA to CG");
				fd.setFilterPath("./");//"C:/Temp");
				String[] filterExt = { "*.cxt" };//, "*.doc", ".rtf", "*.*" };
				fd.setFilterExtensions(filterExt);
				String selected = fd.open();
				//System.out.println(selected);
				FCAtoCGIF(selected);
				dialog("FCA to CG conversion complete",s);
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		}
		openItem.addSelectionListener(new Open());
		saveItem.addSelectionListener(new Save());

		exitItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(s, SWT.ICON_QUESTION
						| SWT.YES | SWT.NO);
				messageBox.setMessage("Do you really want to exit?");
				messageBox.setText("Exiting Application");
				int response = messageBox.open();
				if (response == SWT.YES)
					System.exit(0);
			}
		});
		s.setMenuBar(m);
		
		// add two split panes
		s.setLayout (new FillLayout());
		SashForm form = new SashForm(s,SWT.HORIZONTAL);
		form.setLayout(new FillLayout());
		
		Composite child1 = new Composite(form,SWT.NONE);
		child1.setLayout(new FillLayout());
		
		Composite child2 = new Composite(form,SWT.NONE);
		child2.setLayout(new FillLayout());
	
		form.setWeights(new int[] {50,50});
		
		s.open();

		while (!s.isDisposed()) {
			if (!d.readAndDispatch())
				d.sleep();
		}
		d.dispose();
	}

	public static void main(String[] argv) {
		new CG_FCA_Convertor();
	}

	private int dialog ( String Text, Shell s) {
		// create dialog with ok and cancel button and info icon
		MessageBox dialog = 
				new MessageBox(s, SWT.ICON_QUESTION | SWT.OK| SWT.CANCEL);
		dialog.setText(Text);
		dialog.setMessage("Another conversion?");

		// open dialog and await user selection
		int returnCode = dialog.open(); 
		return returnCode;
	}


	public static void FCAtoCGIF (String FileName) {

		FormalContext<String, String> context = null;

		//first read in the FCA context
		context = BurmeisterFile.read(FileName);

		//now convert the context into CG nodes
		for (int ContextNumber = 0;ContextNumber<context.getObjectCount();ContextNumber++) {
			FullObject<String,String> NewObject = context.getObjectAtIndex(ContextNumber);

			Tripple t=Tripple.get(NewObject.getIdentifier());
			// import all the nodes
			ConceptNode inputCN = ConceptNode.import_txt(t.getInputCNname());
			ConceptNode outputCN = ConceptNode.import_txt(t.getOutputCNname());
			RelationNode.import_txt(NewObject.getIdentifier(), t.getRNName(), inputCN, outputCN);
		}

		//process the RN's
		for (int ContextNumber = 0;ContextNumber<context.getObjectCount();ContextNumber++) {
			FullObject<String,String> NewObject = context.getObjectAtIndex(ContextNumber);
			Set<String> Attributes = NewObject.getDescription().getAttributes();
			Iterator<String> atIterator = Attributes.iterator();
			Tripple t=Tripple.get(NewObject.getIdentifier());
			System.out.println("Working on: " + NewObject.getIdentifier());
			while (atIterator.hasNext()) {
				String Attribute = atIterator.next();
				if (context.objectHasAttribute(NewObject, Attribute)) {
					System.out.println("**********NewObject: " + NewObject.getIdentifier() +" has: " + Attribute);
					if (!ConceptNode.exists(Attribute) && !RelationNode.exists(NewObject.getIdentifier())){
						RelationNode rn = RelationNode.get(NewObject.getIdentifier());
						System.out.println("adding label: " + Attribute + ", to RN: " + rn.getName());
						rn.addLabel(Attribute); // for sorting later
					}
				}
			}
		}
		int end = FileName.indexOf('.');
		String Temp = FileName.substring(0, end);
		FileName = Temp + ".cgif";
		CGIFFile.write(FileName);
	}

	public static void CGIFtoFCA(String FileName) {

		CGIFFile.read(FileName);

		FormalContext<String, String> context = new FormalContext<String, String>();
		//NoExpertFull<String> expert = new NoExpertFull<String>(context);

		// this should be the other way round - go through the RNs!!
		Collection<RelationNode> rns = RelationNode.getRelationNodes();
		RelationNode rn;
		Iterator<RelationNode> RNsIterator = rns.iterator();
		System.out.println("RN size: " + rns.size());
		while (RNsIterator.hasNext()) {
			rn = RNsIterator.next();
			String NewObject =  rn.getName();// rn.getConceptNodeIn().getName() + " " + rn.getName() + " " + rn.getConceptNodeOut().getName();
			System.out.println("Adding Object: " + NewObject);
			try {
				context.addObject(new FullObject<String,String>(NewObject));
			} catch (IllegalObjectException e) {
				e.printStackTrace();
			}

			//first incoming label
			String na = "I_" + rn.getIncomingLabel();
			try {
				System.out.println("Adding Attribute: " + na );
				context.addAttribute(na);
			} catch (IllegalAttributeException e) {
				System.out.println("Attribute already exists: " + na );
			}

			//now add atrr to object
			try {
				context.addAttributeToObject(na, NewObject);
			} catch (IllegalObjectException e) {
				e.printStackTrace();
			}

			//Now outgoing label
			na = "O_" + rn.getOutgoingLabel();
			try {
				System.out.println("Adding Attribute: " + na );
				context.addAttribute(na);
			} catch (IllegalAttributeException e) {
				System.out.println("Attribute already exists: " + na );
			}

			//now add atrr to object
			try {
				context.addAttributeToObject(na, NewObject);
			} catch (IllegalObjectException e) {
				e.printStackTrace();
			}

			//Now incoming CN
			if (rn.getConceptNodeIn()==null)
				System.out.println("rn null?" );

			na = rn.getConceptNodeIn().getName();
			
			try {
				System.out.println("Adding Attribute: " + na );
				context.addAttribute(na);
			} catch (IllegalAttributeException e) {
				System.out.println("Attribute already exists: " + na );
			}


			//now add atrr to object
			try {
				context.addAttributeToObject(na, NewObject);
			} catch (IllegalObjectException e) {
				e.printStackTrace();
			}

			//Now outgoing CN
			na = rn.getConceptNodeOut().getName();
			try {
				System.out.println("Adding Attribute: " + na );
				context.addAttribute(na);
			} catch (IllegalAttributeException e) {
				System.out.println("Attribute already exists: " + na );
			}

			//now add atrr to object
			try {
				try {
					context.addAttributeToObject(na, NewObject);
				} catch (IllegalObjectException e) {
					e.printStackTrace();
				}
			} catch (IllegalAttributeException e) {
				System.out.println("Attribute already exists for object" + na );
			}
		}

		// now for the case where we have a ConceptNode which does'nt have any RNs as input e.g. Transaction
		// we have to go through all the CNs and make sure that there labels (O_) have already been added
		// otherwise the FCA files does not contain it and the conversion FCA-CG will have a null in place of this value
		Collection<ConceptNode> cns = ConceptNode.getConceptNodes();
		ConceptNode cn;
		Iterator<ConceptNode> CNsIterator = cns.iterator();
		while (CNsIterator.hasNext()) {
			cn = CNsIterator.next();
			String Attr = "O_" + cn.getReferent();
			try {
				context.addAttribute(Attr);
				
		        //now we have one
				System.out.println("Adding Attribute: " + Attr );
				// add a dummy
				String NewObject = "null " + "null " + cn.getName();
				System.out.println("Adding Object: " + NewObject);
				try {
					context.addObject(new FullObject<String,String>(NewObject));
				} catch (IllegalObjectException e) {
					e.printStackTrace();
				}

				//now add atrr to object
				Attr = "O_" + cn.getReferent();
				try {
					context.addAttributeToObject(Attr, NewObject);
				} catch (IllegalObjectException e) {
					e.printStackTrace();
				}
				

			} catch (IllegalAttributeException e) {
				//System.out.println("Attribute already exists: " + Attr );
			}
		}


		// get the objects
		IndexedSet<FullObject<String, String>> objects = context.getObjects();
		Iterator<FullObject<String, String>> objectIterator = objects.iterator();
		while (objectIterator.hasNext())
		{
			FullObject<String, String> obj =objectIterator.next();
			System.out.println("Object: " + obj.getIdentifier());
		}

		//get the attributes
		IndexedSet<String> attributes = context.getAttributes();
		Iterator<String> attributeIterator = attributes.iterator();
		while (attributeIterator.hasNext())
		{
			String attr = attributeIterator.next();
			System.out.println("Attrib: " + attr);
		}
		int end = FileName.indexOf('.');
		String Temp = FileName.substring(0, end);
		FileName = Temp + ".cxt";
		BurmeisterFile.write(context,FileName);
	}
}


