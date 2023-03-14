import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * XML class
 * @author Andrii Krasovskyy
 */
public class XML {
	
	//ANSI codes
	static final String ANSI_RESET = "\u001B[0m";
	static final String ANSI_BLACK = "\u001B[30m";
	static final String ANSI_RED = "\u001B[31m";
	static final String ANSI_GREEN = "\u001B[32m";
	static final String ANSI_YELLOW = "\u001B[33m";
	static final String ANSI_BLUE = "\u001B[34m";
	static final String ANSI_PURPLE = "\u001B[35m";
	static final String ANSI_CYAN = "\u001B[36m";
	static final String ANSI_WHITE = "\u001B[37m";
	
	/**
	 * Document
	 */
	public static Document docGlobal;
	
	/**
	 * Writes document in .xml file with specified name
	 * @param doc
	 * @param name
	 * @throws TransformerException
	 */
	public static void writeToXML (Document doc, String name) throws TransformerException {
		doc.normalize();
		File file = new File(name + ".xml");
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(file);
		transformer.transform(source, result);
	}
	
	/**
	 * Reads .xml document with specified name
	 * @param name
	 * @return xmlDoc
	 */
	public static Document readFromXML (String name) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		    Document xmlDoc = dBuilder.parse(name + ".xml");
		    xmlDoc.getDocumentElement().normalize();
		    return xmlDoc;
		} catch (Exception e) {
			System.out.println(ANSI_RED + "Operation unsuccessful: file not found." + ANSI_RESET);
		}
		return null;
	}
	
	/**
	 * Writes University class object to .xml files
	 * @param university
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	public static void toXML (University university) throws ParserConfigurationException, TransformerException {
		
	    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	    Document uDoc = docBuilder.newDocument();
	    
	    Element rootElement = uDoc.createElement("University");
	    uDoc.appendChild(rootElement);
	    
	    for (int i = 0; i < university.faculties.length; i++) {
	    	
	    	Element faculty = uDoc.createElement("faculty");
			faculty.appendChild(uDoc.createTextNode(university.faculties[i].fileName));
			rootElement.appendChild(faculty);
			
			Document fDoc = docBuilder.newDocument();
    	    
    	    Element rootElement1 = fDoc.createElement("faculty");
    	    fDoc.appendChild(rootElement1);
			
	    	for (int j = 0; j < university.faculties[i].departments.length; j++) {
	    		
	    		Element department = fDoc.createElement("department");
				department.appendChild(fDoc.createTextNode(university.faculties[i].departments[j].name));
				rootElement1.appendChild(department);
				
				Document dDoc = docBuilder.newDocument();
	    	    
	    	    Element rootElement2 = dDoc.createElement("department");
	    	    dDoc.appendChild(rootElement2);
	    	    
				for (int k = 0; k < university.faculties[i].departments[j].teachers.length; k++) {
					
					Element teacher = dDoc.createElement("teacher");
					
					teacher.setAttribute("thirname", university.faculties[i].departments[j].teachers[k].thirname);
					teacher.setAttribute("name", university.faculties[i].departments[j].teachers[k].name);
					teacher.setAttribute("fathername", university.faculties[i].departments[j].teachers[k].fathername);
					
					rootElement2.appendChild(teacher);
				}
	    		
				for (int k = 0; k < university.faculties[i].departments[j].students.length; k++) {
					
					Element student = dDoc.createElement("student");
					
					student.setAttribute("thirname", university.faculties[i].departments[j].students[k].thirname);
					student.setAttribute("name", university.faculties[i].departments[j].students[k].name);
					student.setAttribute("fathername", university.faculties[i].departments[j].students[k].fathername);
					
					student.setAttribute("course", Integer.toString(university.faculties[i].departments[j].students[k].course));
					
					rootElement2.appendChild(student);
				}
				writeToXML(dDoc, university.faculties[i].departments[j].name);
	    	}
	    	writeToXML(fDoc, university.faculties[i].fileName);
	    }
	    writeToXML(uDoc, "University");
	}
	
	/**
	 * Converts .xml files to University class object
	 * @return university
	 */
	public static University fromXML () {
		Document uDoc = readFromXML ("University");
		
		University university = new University("NaUKMA", uDoc.getElementsByTagName("faculty").getLength());
		
		for (int i = 0; i < uDoc.getElementsByTagName("faculty").getLength(); i++) {
			String uName = uDoc.getElementsByTagName("faculty").item(i).getTextContent();
			
			Document fDoc = readFromXML (uName);
	
			university.faculties[i] = new Faculty(uName, uName, fDoc.getElementsByTagName("department").getLength());
			
			for (int j = 0; j < fDoc.getElementsByTagName("department").getLength(); j++) {
				String dName = fDoc.getElementsByTagName("department").item(j).getTextContent();
				
				Document dDoc = readFromXML(dName);
				
				university.faculties[i].departments[j] = new Department(dName, dDoc.getElementsByTagName("teacher").getLength(), dDoc.getElementsByTagName("student").getLength());
				
				for (int k = 0; k < dDoc.getElementsByTagName("teacher").getLength(); k++) {
					
					String thirname = dDoc.getElementsByTagName("teacher").item(k).getAttributes().getNamedItem("thirname").getNodeValue();
					
					String name = dDoc.getElementsByTagName("teacher").item(k).getAttributes().getNamedItem("name").getNodeValue();
					
					String fathername = dDoc.getElementsByTagName("teacher").item(k).getAttributes().getNamedItem("fathername").getNodeValue();
					
					university.faculties[i].departments[j].teachers[k] = new Teacher(thirname, name, fathername);
				}
				
				for (int k = 0; k < dDoc.getElementsByTagName("student").getLength(); k++) {
					
					String thirname = dDoc.getElementsByTagName("student").item(k).getAttributes().getNamedItem("thirname").getNodeValue();
					
					String name = dDoc.getElementsByTagName("student").item(k).getAttributes().getNamedItem("name").getNodeValue();
					
					String fathername = dDoc.getElementsByTagName("student").item(k).getAttributes().getNamedItem("fathername").getNodeValue();
					
					int course = Integer.parseInt(dDoc.getElementsByTagName("student").item(k).getAttributes().getNamedItem("course").getNodeValue());
					
					university.faculties[i].departments[j].students[k] = new Student(thirname, name, fathername, course);
				}
				
			}
			
		}
		return university;
	}
}









