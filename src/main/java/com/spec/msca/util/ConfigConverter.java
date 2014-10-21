package com.spec.msca.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.spec.util.Collection_Util;
import com.spec.util.StringPair;
import com.spec.util.String_Util;
import com.spec.util._debug;

/**
 * Convert sca_Configuration file to 
 * Automatic generate two configuration files findbugs.xml and messages.xml
 * 
 * @author wli001
 *
 */
public class ConfigConverter {	
	final private String configDir		= "src\\main\\resources\\";
	final private String findbugsXML	= configDir + 
											"findbugs.xml";
	final private String messagesXML	= configDir + 
											"messages.xml";
	final private String scaXML			= configDir + "class-bugs.xml";
		
	private HashMap<String, String> classBugreportsMap	= new HashMap<String, String>();
	private HashMap<String, String> classDetailPair		= new HashMap<String, String>();
	private HashMap<String, String> BugAbbrevMap		= new HashMap<String, String>();
	private HashMap<String, String> BugShortDescPair	= new HashMap<String, String>();
	private HashMap<String, String> BugLongDescPair		= new HashMap<String, String>();
	private HashMap<String, String> BugDetailsPair		= new HashMap<String, String>();
	private HashMap<String, String> BugCodePair			= new HashMap<String, String>();
	
	
	public static void main(String args[]) throws IOException, ParserConfigurationException, SAXException, TransformerException{
		ConfigConverter cu	= new ConfigConverter();
		cu.parseSCAConfigFile();
		cu.createFindbugsXML();	
		cu.createMessagesXML();
		
		System.out.println("configuration convertion done.");
	}
	
	private void createMessagesXML() throws ParserConfigurationException, TransformerException{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		Document doc	= docBuilder.newDocument();
		Element root	= doc.createElement("MessageCollection");
		root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.setAttribute("xsi:noNamespaceSchemaLocation", "messagecollection.xsd");
		doc.appendChild(root);
		
		Element plugin	= doc.createElement("Plugin");		
		root.appendChild(plugin);
		
		Element shortDesc	= doc.createElement("ShortDescription");
		shortDesc.setTextContent("Spec SCA Security Detector.");
		plugin.appendChild(shortDesc);
		
		Element details		= doc.createElement("Details");
		details.setTextContent("Provides detectors to detect security bugs.");
		plugin.appendChild(details);
				
		for (String key: classDetailPair.keySet()){			
			Element detector	= doc.createElement("Detector");
			detector.setAttribute("class", key);
			
			Element cDetailsEle	= doc.createElement("Details");
			cDetailsEle.setTextContent(classDetailPair.get(key));
			detector.appendChild(cDetailsEle);
			
			root.appendChild(detector);
		}
		
		for (String bType: BugAbbrevMap.keySet()){			
			String bCode		= BugAbbrevMap.get(bType);
			String bShortDesc	= BugShortDescPair.get(bType);
			String bLongDesc	= BugLongDescPair.get(bType);
			String bDetails 	= BugDetailsPair.get(bType);
			String bugCode		= BugCodePair.get(bType);
			
			Element bPattern	= doc.createElement("BugPattern");
			root.appendChild(bPattern);
			
			bPattern.setAttribute("type", bType);
			
			Element sDesc	= doc.createElement("ShortDescription");
			sDesc.setTextContent(bShortDesc);
			bPattern.appendChild(sDesc);
			
			Element lDesc	= doc.createElement("LongDescription");
			lDesc.setTextContent(bLongDesc);
			bPattern.appendChild(lDesc);
			
			Element bDetailsEle	= doc.createElement("Details");
			bDetailsEle.setTextContent(bDetails);
			bPattern.appendChild(bDetailsEle);
			
			Element bCodeEle	= doc.createElement("BugCode");
			bCodeEle.setAttribute("abbrev", bCode);
			bCodeEle.setTextContent(bugCode);
			
			root.appendChild(bCodeEle);			
		}
		
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(messagesXML));
		transformer.transform(source, result);
	}
	
	private void createFindbugsXML() throws ParserConfigurationException, TransformerException{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		// root elements
		Document doc = docBuilder.newDocument();
		Element root = doc.createElement("FindbugsPlugin");
		doc.appendChild(root);
		
		Attr xmlns	= doc.createAttribute("xmlns:xsi");
		xmlns.setValue("http://www.w3.org/2001/XMLSchema-instance");
		root.setAttributeNode(xmlns);
		
		Attr xsi	= doc.createAttribute("xsi:noNamespaceSchemaLocation");
		xsi.setValue("findbugsplugin.xsd");
		root.setAttributeNode(xsi);
		
		Attr pluginid	= doc.createAttribute("pluginid");
		pluginid.setValue("com.spec.sca.crypto");
		root.setAttributeNode(pluginid);
		
		Attr provider	= doc.createAttribute("provider");
		provider.setValue("Spec Paul Li");
		root.setAttributeNode(provider);
		
		Attr defaultenabled	= doc.createAttribute("defaultenabled");
		defaultenabled.setNodeValue("true");
		root.setAttributeNode(defaultenabled);
		
		Attr website	= doc.createAttribute("website");
		website.setNodeValue("");
		root.setAttributeNode(website);
		
		for (String key : classBugreportsMap.keySet()){
			Element detector	= doc.createElement("Detector");
			Attr className 	= doc.createAttribute("class");
			className.setValue(key);
			
			Attr reports	= doc.createAttribute("reports");
			reports.setValue(classBugreportsMap.get(key));
			
			detector.setAttributeNode(className);
			detector.setAttributeNode(reports);
			
			root.appendChild(detector);
		}
		
		for (String key: BugAbbrevMap.keySet()){
			Element bugPattern = doc.createElement("BugPattern");
			Attr type		= doc.createAttribute("type");
			type.setValue(key);
			bugPattern.setAttributeNode(type);
			
			Attr abbrev		= doc.createAttribute("abbrev");
			abbrev.setValue(BugAbbrevMap.get(key));
			bugPattern.setAttributeNode(abbrev);
			
			Attr cagegory	= doc.createAttribute("category");
			cagegory.setValue("SECURITY");			
			bugPattern.setAttributeNode(cagegory);
			
			root.appendChild(bugPattern);
		}
		
		
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(findbugsXML));
		transformer.transform(source, result);
		
	}
	
	private void parseSCAConfigFile() throws IOException, ParserConfigurationException, SAXException{
		File fXmlFile = new File(scaXML);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
				
		NodeList nList = doc.getElementsByTagName("class");	
		for (int i = 0; i <nList.getLength(); i++){
			Node nNode = nList.item(i);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement	= (Element) nNode;
				String cName		= eElement.getAttribute("name");	
				String cDetails		= eElement.getElementsByTagName("details").item(0).getTextContent();
				classDetailPair.put(cName, cDetails);
			
				StringBuffer bugReports	= new StringBuffer();				
				NodeList bugList	= eElement.getElementsByTagName("bug");
				for (int j = 0; j < bugList.getLength(); j++){					
					Node bugNode	= bugList.item(j);
					//get report types
					Element bugElement	= (Element)bugNode;
					String bugType	= bugElement.getAttribute("type");
					bugReports.append(bugType + ", ");
					
					//get bug abbrev
					String bugAbbrev	= bugElement.getElementsByTagName("abbrev").item(0).getTextContent();
					BugAbbrevMap.put(bugType, bugAbbrev);
					
					//get bug short description
					String shortDesc	= bugElement.getElementsByTagName("ShortDescription").item(0).getTextContent();
					BugShortDescPair.put(bugType, shortDesc);
					
					//get bug long description
					String longDesc		= bugElement.getElementsByTagName("LongDescription").item(0).getTextContent();
					BugLongDescPair.put(bugType, longDesc);
					
					//get bug details
					String details		= bugElement.getElementsByTagName("Details").item(0).getTextContent();
					BugDetailsPair.put(bugType, details);
					
					//get bug code
					String code			= bugElement.getElementsByTagName("BugCode").item(0).getTextContent();
					BugCodePair.put(bugType, code);
					
				}
				
				String bugTypes = String_Util.truncateLastChar(bugReports.toString().trim());
//				_debug.println(bugTypes);
				classBugreportsMap.put(cName, bugTypes);
			}
		}
		
//		_debug.println(StringPair.ListToString(BugAbbrevPair));
//		_debug.println(StringPair.ListToString(BugShortDescPair));
//		_debug.println(StringPair.ListToString(BugLongDescPair));
//		_debug.println(StringPair.ListToString(BugDetailsPair));
//		_debug.println(StringPair.ListToString(BugCodePair));
//		_debug.println(StringPair.ListToString(classDetailPair));
//		_debug.println(StringPair.ListToString(classBugreportsPair));
		
	}
}