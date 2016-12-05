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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.aii.mapping.api.StreamTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;

/**
 * Implemtação em Java do programa Abap Z_TRATA_REGISTRADOR_FATURADO_O
 * 
 * @author Dorival Querino da Silva
 * 
 *         - 5 de dez de 2016 - CSC
 * 
 */
public class ZTrataFalhaRegistradorFaturadoO implements StreamTransformation {
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

			NodeList nList = document.getElementsByTagName("value-list");

			Node node = nList.item(0);
			if (node != null && node.hasChildNodes()) {
				NodeList childNodes = node.getChildNodes();
				for (int i = 0; i < childNodes.getLength(); i++) {
					// recupera as tags <val>
					Node childNode = childNodes.item(i);
					NamedNodeMap attributes = childNode.getAttributes();
					if (attributes != null && attributes.getNamedItem("lb") != null
							&& attributes.getNamedItem("lb").getNodeValue().contains("*")) {
						String[] split = attributes.getNamedItem("lb").getNodeValue().split("\\*");
						attributes.getNamedItem("lb").setNodeValue(split[0]);
					}

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
