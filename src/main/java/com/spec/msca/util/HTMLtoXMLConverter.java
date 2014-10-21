package com.spec.msca.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//import com.spec.util._debug;
import com.spec.util.File_Util;

/**
 * Convert a Findbugs report in HTML to XML 
 * @author wli001
 *
 */
public class HTMLtoXMLConverter {
	public org.jsoup.nodes.Document parseHTML(String htmlFileName) throws IOException{
		String htmlString = File_Util.writeFileToString(htmlFileName);
		
		return Jsoup.parse(htmlString);
	}
	
	public void createXML(org.jsoup.nodes.Document jsoupDoc, String outputXMLFileName){
		 
		  try {	 
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			/** root elements */
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("findbugs-report");
			doc.appendChild(rootElement);
	 
			/*** overview elements */
			Element overviewElement = doc.createElement("overview");
			rootElement.appendChild(overviewElement);
			
			Element projectInfoElement	= doc.createElement("project-information");
			overviewElement.appendChild(projectInfoElement);
			
			Element metricsElement		= doc.createElement("metrics");
			overviewElement.appendChild(metricsElement);
			
			Elements jsoupPElements	= jsoupDoc.getElementsByTag("p");
			
			Iterator<org.jsoup.nodes.Element> elementIter	= jsoupPElements.iterator();
			while (elementIter.hasNext()){
				org.jsoup.nodes.Element 
					jsoupPElement	= elementIter.next();
				String jsoupString 	= this.getJsoupNodeText(jsoupPElement);
				
				Element currElement = null;
				
				//create project information
				if (jsoupString.startsWith("Project:")) {
					currElement = doc.createElement("project-name");			
					jsoupString = jsoupString.substring(8, jsoupString.length()).trim();
				}else if (jsoupString.startsWith("FindBugs version:")){
					currElement = doc.createElement("fingbugs-version");
					jsoupString = jsoupString.substring(17, jsoupString.length()).trim();
				}else if (jsoupString.startsWith("Code analyzed:")){
					currElement = doc.createElement("source-code-repository");
					jsoupString	= this.getJsoupNodeText(jsoupPElement.nextElementSibling().child(0));					
				}
				
				if (currElement != null){
					currElement.appendChild(doc.createTextNode(jsoupString));
					projectInfoElement.appendChild(currElement);
				}
				
				//create metrics
				boolean isMetricsBuilt = false;
				if (jsoupPElement.previousElementSibling() != null &&
						jsoupPElement.previousElementSibling().ownText().equals("Metrics") )
				{
					Element sourceStatElement	= doc.createElement("analysis-stat");
					sourceStatElement.appendChild(doc.createTextNode(jsoupPElement.ownText()));
					metricsElement.appendChild(sourceStatElement);
					
					Element metricsTable	= doc.createElement("metrics-table");
					metricsElement.appendChild(metricsTable);
					org.jsoup.nodes.Element metricsTableElement = jsoupPElement.nextElementSibling();
					
					List<Node> metricsTableRows	= metricsTableElement.childNodes().get(0).childNodes();
					for (int i=1; i<metricsTableRows.size(); i++){
						Node row = metricsTableRows.get(i);
						List<Node> col = row.childNodes();			
						String priority = this.makeLegalXMLChar(getJsoupNodeText(col.get(0).childNode(0)));					
						Element priorityElement = doc.createElement(priority);
						
						String total 	= this.getJsoupNodeText(col.get(1));
						String density	= this.getJsoupNodeText(col.get(2));
						priorityElement.setAttribute("total", total);
						priorityElement.setAttribute("density",density );
						
						metricsTable.appendChild(priorityElement);
					}
					isMetricsBuilt = true;
				}
				
				if (isMetricsBuilt) break;
			}
			/*** overview elements done*/
			
			/*** findings elements */
			Elements jsoupTableElements = jsoupDoc.getElementsByTag("table");
			Elements findingElements 	= jsoupTableElements.get(2).children().get(0).children();
			
			Element findingsElement	= doc.createElement("findings");
			rootElement.appendChild(findingsElement);

			for (int i=1; i<findingElements.size(); i++){
				org.jsoup.nodes.Element findingRow = findingElements.get(i);
				
				if (findingRow.hasAttr("onclick")) {
//					_debug.println(findingRow);	
					
					//create a new finding nodes
					Element findingElement		= doc.createElement("finding");
					findingsElement.appendChild(findingElement);
					
					Element vulnerCode	= doc.createElement("code");
					vulnerCode.appendChild(doc.createTextNode(this.getJsoupNodeText(findingRow.childNode(0))));
					
					Element description	= doc.createElement("description");
					description.appendChild(doc.createTextNode(this.getJsoupNodeText(findingRow.childNode(1))));
					
					findingElement.appendChild(vulnerCode);
					findingElement.appendChild(description);					
					
					while(true){											
						if (i+1== findingElements.size()) {
//							_debug.println("break i reach");
							break;						
						}
						org.jsoup.nodes.Element nextRow = findingElements.get(i+1);
						if (nextRow.hasAttr("onclick")) {
//							_debug.println("break onclick");
							break;	
						}
						i++;
						
						/*
							<a href="#DMI_EMPTY_DB_PASSWORD">Bug type DMI_EMPTY_DB_PASSWORD (click for details)</a>, 
							<br />, 
							In class org.owasp.webgoat.session.DatabaseUtilities, 
							<br />, 
							In method org.owasp.webgoat.session.DatabaseUtilities.getHsqldbConnection(String, WebgoatContext), 
							<br />, 
							At DatabaseUtilities.java:[line 112]
						*/						
						List<Node> findDetails	= nextRow.childNode(1).childNode(0).childNodes();

						Node linkToExplanationNode	= findDetails.get(0);
						Node classNameNode			= findDetails.get(2);
						Node methodNameNode			= findDetails.get(4);
//						
						Element linkElement			= doc.createElement("link");
						linkElement.appendChild(doc.createTextNode(linkToExplanationNode.toString()));
						
						Element classNameElement 	= doc.createElement("class");
						classNameElement.appendChild(doc.createTextNode(processClassName(classNameNode)));
						
						Element methodNameElement	= doc.createElement("method");
						methodNameElement.appendChild(doc.createTextNode(processMethodName(methodNameNode)));
						
						findingElement.appendChild(classNameElement);
						findingElement.appendChild(classNameElement);
						findingElement.appendChild(methodNameElement);				
						
						for (int j=6; j<findDetails.size(); j++){
							Element lineElement	= doc.createElement("line");
							Node lineNumberNode	= findDetails.get(j);
							if (lineNumberNode.nodeName().equals("br")) continue;
							lineElement.appendChild(doc.createTextNode(processLineNumber(lineNumberNode)));
							findingElement.appendChild(lineElement);							
						}
					}
					
				}	
				
			}
			/*** findings elements done*/
				 
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = null;
			
			result = new StreamResult(new File(outputXMLFileName));
	 
			// Output to console for testing//			 
//			result = new StreamResult(System.out);
	 
			transformer.transform(source, result);
	 
//			System.out.println("File saved!");
	 
		  } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
		  }
		}
	
	private String getJsoupNodeText(Node n){
		return Jsoup.parse(n.toString()).body().text();
	}
	
	private String processClassName(Node classNameNode){
		String className = classNameNode.toString();
		return className.substring(8, className.length()).trim();		
	}
	
	private String processMethodName(Node methodNode){
		String methodName = methodNode.toString();
		return methodName.substring(9, methodName.length()).trim();
	}
	
	private String processLineNumber(Node lineNumberNode){
		String lineNumber = lineNumberNode.toString();
		int sIndex	= lineNumber.lastIndexOf(' ');
		int eIndex	= lineNumber.indexOf(']');
		
		if (eIndex < 0) return "unknown";
		else return lineNumber.substring(sIndex+1, eIndex).trim();		
	}
	
	private String makeLegalXMLChar(String input){
		input = input.replace('*', ' ');
		input = input.replace(' ', '-');
		return input.toLowerCase();
	}
}
