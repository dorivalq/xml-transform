package teste.sax;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.sap.aii.mapping.api.StreamTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;

@SuppressWarnings("rawtypes")
public class RegistradorSaxHandler extends DefaultHandler implements StreamTransformation {
	private Map param;
	private Document document;
	private String ant;

	@Override
	public void setParameter(Map param) {
		this.param = param;
		if (param == null)
			this.param = new HashMap();
	}

	@Override
	public void execute(InputStream inputStream, OutputStream outputStream) throws StreamTransformationException {
		try {
			SAXParserFactory fact = SAXParserFactory.newInstance();
			SAXParser sp = fact.newSAXParser();
			RegistradorSaxHandler handler = new RegistradorSaxHandler();
			
			
			sp.parse(inputStream, handler);
			
			
			
			
			
			
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			this.document = builder.parse(inputStream);//<C_C212>
			NodeList nList = document.getElementsByTagName("D_7140");
					//ElementsByTagName("<D_7140>").;
			System.out.println(" list length: " + nList.getLength());
			String ori = null;
			String wstring = "";
			// LOOP NAS TAGS "<D_7140>"
			for (int i = 0; i < nList.getLength(); i++) {
				Node node = nList.item(i);
				// String ori = "<D_7140>1-1:1.9.1*Ts:1.1</D_7140>";
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					if (node.hasChildNodes()) {
						// String ori = "<D_7140>DO*A</D_7140>";
						ori = node.getFirstChild().getTextContent().trim();
					}
				}

				wstring = ori.substring(0, ori.indexOf("*") + 1);
				// remove espacos
				wstring = wstring.replaceAll(" ", "");

				// Se tem *, troca o valor do node pela substringde 0 ate *
				if (ori.contains("*")) {
					node.setTextContent(wstring);
					System.out.println("Node alterado: " + node.getTextContent());
				}
			}

			// Escreve o xml de saida
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			StreamResult streamResult = new StreamResult(outputStream);
			DOMSource source = new DOMSource(document);
			transformer.transform(source, streamResult);

		} catch (Exception e) {
			e.printStackTrace();
			throw new StreamTransformationException("Falha ao processar o mapping ", e);
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("D_7140")) {
			System.out.println("Anterior: "+ant);
			System.out.println("qName: "+qName);
			this.ant = qName;
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		System.out.println("EndElement: qname= "+qName );
	}
	public static void main(String[] args) throws Exception {
		
		System.out.println("Inicio: " +new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
		long timeInMillis = Calendar.getInstance().getTimeInMillis();
		RegistradorSaxHandler reg = new RegistradorSaxHandler();

		InputStream inputStream = new FileInputStream(
				new File("D:\\desenv\\CPFL\\Workspace\\trata-falha\\trata-falha-registrador.xml"));
		OutputStream outputStream = new FileOutputStream(
				new File("D:\\desenv\\CPFL\\Workspace\\trata-falha\\trata-falha-registrador-OUT.xml"));

		reg.execute(inputStream, outputStream);
		
		System.out.println("Fim: " +new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
		long timeInMillis2 = Calendar.getInstance().getTimeInMillis();
		System.out.println("Tempo gasto = " +(Double.valueOf(timeInMillis2 - timeInMillis)/1000) + " segundo(s)");
	}
}
