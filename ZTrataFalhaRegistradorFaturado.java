package br.com.cpfl.mapping;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.aii.mapping.api.StreamTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;

/**
 * Implemta��o em Java do programa Abap Z_TRATA_REGISTRADOR_FATURADO
 * 
 * @author Dorival Querino da Silva -
 * 
 *         30 de Nov de 2016 - CSC
 * 
 */
public class ZTrataFalhaRegistradorFaturado implements StreamTransformation {
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
			NodeList nList = document.getElementsByTagName("D_7140");

			String origem = null;
			// LOOP NAS TAGS "<D_7140>"
			for (int i = 0; i < nList.getLength(); i++) {
				Node node = nList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					if (node.hasChildNodes()) {
						origem = node.getFirstChild().getTextContent().trim();
					}
				}


				// Se tem '*' no value, 
				if (origem.contains("*")) {
					// troca o valor do node pela sua substring (0 ate '*') e removendo os espa�os
					origem = origem.substring(0, origem.indexOf("*") + 1).replaceAll(" ", "");
					node.setTextContent(origem);
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
}
