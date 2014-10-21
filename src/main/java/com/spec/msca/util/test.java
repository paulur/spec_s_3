package com.spec.msca.util;

import java.io.IOException;

import org.jsoup.nodes.Document;

import com.spec.util._debug;

public class test {
	public static void main(String args[]) throws IOException{
		String htmlDir		= "resources//";
		String htmlFileName = htmlDir + "report.html";
		String xmlFileName	= htmlDir + "report.xml";
		
		HTMLtoXMLConverter h2x	=  new HTMLtoXMLConverter();
		Document doc = h2x.parseHTML(htmlFileName);
		
		h2x.createXML(doc, xmlFileName);
		
//		_debug.print(doc);
		
//		Debug.print(doc.getElementsByTag("h1").last().childNodeSize());
	}
}