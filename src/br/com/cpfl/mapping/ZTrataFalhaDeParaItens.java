package br.com.cpfl.mapping;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.aii.mapping.api.StreamTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;

@SuppressWarnings("rawtypes")
public class ZTrataFalhaDeParaItens implements StreamTransformation {
	private Map param;
	private Document document;

	@Override
	public void setParameter(Map param) {
		this.param = param;
		if (param == null)
			this.param = new HashMap();
	}

	@Override
	public void execute(InputStream inputStream, OutputStream outputStream) throws StreamTransformationException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			this.document = builder.parse(inputStream);
			NodeList nList = document.getElementsByTagName("*");

			for (int i = 0; i < nList.getLength(); i++) {
				Node node = nList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					if (node.hasChildNodes()) {
						if (node.getFirstChild() != null 
								&& node.getFirstChild().getTextContent().contains("*")) {
							
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new StreamTransformationException("Falha ao efetuar o mapping: ", e);
		}
	}

}
