package statistikplugin;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;


import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JWindow;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.util.zip.*;

public class Utils {
	/**
	 * 
	 */
	private static JFrame frame;
	
	public static String vorschlagname = "";
	
	public static org.w3c.dom.Document daten;

	/**
	 * Zeigt ein Dialogfenster an
	 * 
	 * @param s Ausgabe String
	 * @param option Art des Fenstes
	 */
	public static void dialog(String s, int option) {
		String title = "";
		if (option == JOptionPane.ERROR_MESSAGE) {
			title = "Fehler";
		}
		if (option == JOptionPane.INFORMATION_MESSAGE) {
			title = "Information";
		}
        JOptionPane.showMessageDialog(frame, 
                s,
                title,
                option);
                
		
	}

	/**
	 * @param frame the frame to set
	 */
	public static void setFrame(JFrame frame) {
		Utils.frame = frame;
	}

    public static boolean download(String from, String to) {
    	return download(from, to, false);
    }
	
    public static boolean download(String from, String to, boolean ueberschreiben) {
    	try {
    		URL url = new URL(from);
    		HttpURLConnection urlConn;
    		urlConn = (HttpURLConnection) url.openConnection();
    		String filename = urlConn.getURL().getFile();
    		if (filename.equalsIgnoreCase("")) {
    			Utils.dialog("Ungültiger Dateiname!", JOptionPane.ERROR_MESSAGE);
    			return false;
    		}
    		File file = new File(to);
    		if (file.exists() && !ueberschreiben) {
    			Utils.dialog("Datei " + to + " existiert bereits", 
    						JOptionPane.ERROR_MESSAGE);
    			return false;
    		}
    		if (file.exists()) {
                if (JOptionPane.showConfirmDialog(Utils.getFrame(),
                        "Die Datei " + to + " existiert schon.\n"
                        + "Nochmal herunterladen?",
                		"Nochmal herunterladen", 
                        JOptionPane.YES_NO_OPTION) 
                        == JOptionPane.NO_OPTION) {
                			return true;
                }
    		}

        urlConn.setAllowUserInteraction(true);
        urlConn.setRequestMethod("GET");
        urlConn.setDoInput(true);
        urlConn.setDoOutput(true);
        urlConn.setUseCaches(false);
        urlConn.setDefaultUseCaches(false);
        urlConn.setInstanceFollowRedirects(false);
        urlConn.addRequestProperty("User-Agent", "Heldenbogen V0.99");

        urlConn.connect();
        
        // Auf Länge testen, wenn vorhanden und eine Dateilänge vormittelt wurde
        /*
        List<String> liste = urlConn.getHeaderFields().get("Content-Length"); 
        if ((liste != null) && (liste.size() > 0) && file.exists()) {
        	long laenge = Integer.parseInt(liste.get(0));
        	System.out.println(to + " " + laenge + " " + file.length());
        	if (file.length() == laenge) {
                if (JOptionPane.showConfirmDialog(Utils.getFrame(),
                        "Die Datei " + to + " existiert anscheinend schon und\n"
                        + "wurde wahrscheinlich auch nicht geändert.\n\n"
                        + "Trotzdem herunterladen?",
                		"Nochmal herunterladen", 
                        JOptionPane.YES_NO_OPTION) 
                        == JOptionPane.NO_OPTION) {
                			return true;
                }
        		
        	}
        }
        */
        
        
		DataInputStream dis = new DataInputStream(urlConn.getInputStream());

        
        
        int string = -1;
        FileOutputStream fos = new FileOutputStream(file.getAbsoluteFile());
        byte[] b = new byte[1024];

        JProgressBar progressBar;
        progressBar = new JProgressBar(0, urlConn.getContentLength());
        progressBar.setValue(0);
        progressBar.setString("Download...");
        progressBar.setStringPainted(true);
        JWindow edit = new JWindow(frame);
		//JDialog edit = new JDialog(frame, "Test", true);
		edit.getContentPane().add(progressBar);
		//edit.add(progressBar);
		//edit.add(new JLabel("Test"));
		edit.setSize(200, 50);
		edit.setVisible(true);
        Dimension schirm = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension groesse = edit.getSize();
        edit.setLocation((schirm.width - groesse.width) / 2,
                            (schirm.height - groesse.height) / 2);     
		
		int size = 0;
        while ((string = dis.read(b)) != -1) {
        	System.out.println("" + size + " " + string + " " + urlConn.getContentLength());
        	size += string;
            fos.write(b, 0, string);
            progressBar.setValue(size);
            edit.update(edit.getGraphics());
        }
        fos.flush();
        fos.close();
        dis.close();
        edit.setVisible(false);
        
    	} catch (Exception e) {
    		Utils.dialog("Fehler beim Herunterladen der Datei:\n" + from 
    				+ "\n" + e.getLocalizedMessage(),
    				JOptionPane.ERROR_MESSAGE);
    		return false;
    	}
    	return true;
    		

    	
    }
    

    public static boolean unzip(String zipfile, String zieldir, boolean forceLower) {
       final int BUFFER = 20048;
       int count;
       byte data[] = new byte[BUFFER];
          try {
             BufferedOutputStream dest = null;
             FileInputStream fis = new FileInputStream(zipfile);
             ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
             ZipEntry entry;
             while ((entry = zis.getNextEntry()) != null) {
                // write the files to the disk
                String dateiname;
                if (forceLower) {
                	dateiname = zieldir + entry.getName().toLowerCase();
                } else {
                	dateiname = zieldir + entry.getName();
                }
                FileOutputStream fos = new FileOutputStream(dateiname);
                dest = new BufferedOutputStream(fos, BUFFER);
                while ((count = zis.read(data, 0, BUFFER)) 
                  != -1) {
                   dest.write(data, 0, count);
                }
                dest.flush();
                dest.close();
             }
             zis.close();
          } catch (Exception e) {
             e.printStackTrace();
             return false;
          }
          return true;
       }

	/**
	 * @return the frame
	 */
	public static JFrame getFrame() {
		return frame;
	}

	/**
	 * Vergleicht, ob in beiden Array der gleiche String vorkommt.
	 *
	 * @param a Array 1
	 * @param b Array 2
	 * @return Existiert ein idx1 und idx2, so dass a[idx1].equals(b[idx2]) && !a[idx1].equals("")
	 */
	public static boolean bereichsVergleiche(String[] a, String[] b) {
		for (int i = 0; i < a.length; i++) {
			if (a[i].equals("")) {
				continue;
			}
			for (int j = 0; j < b.length; j++) {
				if (b[j].equals("")) {
					continue;
				}
				if (a[i].equalsIgnoreCase(b[j])) {
					return true;
				}
			}
		}
		return false;
	}
    
    /**
     * 
     * @return gewählter Filename, bei Abbruch ""
     */
    public static String fileDialog(String endung) {
        JFileChooser jfc = new JFileChooser(".");
        File vorschlag = new File(vorschlagname + endung);
        jfc.setSelectedFile(vorschlag);
        if (jfc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            String out = jfc.getSelectedFile().toString();
            if (!out.endsWith(endung)) {
            	out += endung;
            }
            return out;
        }
        return "";
    }
    
	public static String getAtt(Node n, String s) {
		if (n.getAttributes().getNamedItem(s) == null) {
			return "";
		}
		return n.getAttributes().getNamedItem(s).getNodeValue();
	}
	
	/**
	 * Liest ein Attribut aus einem XML-NOde
	 * @param n Node
	 * @param s Attribute Name
	 * @return Inhalt des Attributes nach Int gewandelt.
	 */
	public static Integer getAttInt(Node n, String s) {
		if (n.getAttributes().getNamedItem(s) == null) {
			return 0;
		}
		return Integer.parseInt(n.getAttributes().getNamedItem(s)
				.getNodeValue());
	}

	public static Float getAttFloat(Node n, String s) {
		if (n.getAttributes().getNamedItem(s) == null) {
			return 0.0f;
		}
		return Float.parseFloat(n.getAttributes().getNamedItem(s)
				.getNodeValue());
	}
    static public NodeList getDaten(Node n) {
        String search = getAtt(n, "xpath");
        if (search.equals("")) {
            return null;
        }
        return getDaten(search);
        
    }
    static public NodeList getDaten(String search) {

        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            //search = search.replace("'", "\"");
            XPathExpression expr = xpath.compile(search);
            Object result = expr.evaluate(daten, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;
            return nodes;
        } catch (Exception ex) {
            System.out.println("Fehlerhafter xpath-Ausdruck: " + search);
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            return null;
            //return getDatenAsString(search);
        }
    }

    static public String getDatenAsString(String search) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            //search = search.replace("'", "\"");
            XPathExpression expr = xpath.compile(search);
            Object result = expr.evaluate(daten, XPathConstants.STRING);
            return (String) result;
        } catch (Exception ex) {
            System.out.println("Fehlerhafter xpath-Ausdruck: " + search);
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            return null;
        }
        
    }

    /**
     * Speichert ein XML-Datei als Dokument.
     * @param doc
     */
    static public String xml2string(org.w3c.dom.Document doc) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);

            return result.getWriter().toString();
          } catch(Exception ex ) {
            System.out.println( ex );
            return "";
          }

    }
    static public void show(String filename) {
    try {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                File f = new File(filename);
                desktop.browse(f.toURI());
            }
        } 
    } catch (Exception e) {
        System.out.println(e.toString());
        //
    }
    }

    public static int getDatenAsInt(String string) {
        try {
            return Integer.parseInt(getDatenAsString(string));
        } catch (Exception e) {
            return 0;
        }
    }

    public static GridBagConstraints getGridBagConstraints(int x, int y, int width) {
        GridBagConstraints g = getGridBagConstraints(x, y);
        g.gridwidth = width;
        return g;
    }

    public static GridBagConstraints getGridBagConstraints(int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5,5,5,5);
        c.gridx = x;
        c.gridy = y;
        return c;
    }
//    SpinnerListModel monthModel = ;

    
    public static JSpinner getSpinner(JPanel jp, String label, int x, int y, int akt, int min, int max, int steps) {
        jp.add(new JLabel(label), Utils.getGridBagConstraints(x++, y));

        JSpinner s = new JSpinner(new SpinnerNumberModel(akt, min, max, steps));
        jp.add(s, Utils.getGridBagConstraints(x++, y));
        return s;        
    }

    public static JSpinner getSpinner(JPanel jp, String label, int x, int y, String[] strings) {
        jp.add(new JLabel(label), Utils.getGridBagConstraints(x++, y));

        JSpinner s = new JSpinner(new SpinnerListModel(strings));
        Dimension d = new Dimension(100, s.getMinimumSize().height); 
        s.setMinimumSize(d);
        s.setPreferredSize(d);
        jp.add(s, Utils.getGridBagConstraints(x++, y));
        return s;        
    }

    public static JSpinner getSpinner(JPanel jp, String label, int x, int y, int akt, int min, int max) {
        return getSpinner(jp, label, x, y, akt, min, max,1);
    }

    public static JComboBox getComboBox(JPanel jp, String string, int x, int y, String[] anwendung) {
        jp.add(new JLabel(string), Utils.getGridBagConstraints(x++, y));

        JComboBox s = new JComboBox(anwendung);
        jp.add(s, Utils.getGridBagConstraints(x++, y));
        return s;        
    }
    

}
