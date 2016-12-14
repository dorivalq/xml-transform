package br.com.cpfl.mapping;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.aii.mapping.api.StreamTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;

public class ZZfaXiMdmProvisionamentoR implements StreamTransformation {
	private Map map;
	private Document document;

	@Override
	public void setParameter(Map map) {
		this.map = map;
		if (map == null) {
			this.map = new HashMap();
		}
	}

	@Override
	public void execute(InputStream inputStream, OutputStream outputStream) throws StreamTransformationException {
		String w_match_result = null;
		String w_serialNumber = null;
		try {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
			document = documentBuilder.parse(inputStream);

			NodeList verbList = document.getElementsByTagName("tns:verb");
			Element nodeVerb = (Element) verbList.item(0);
			String valorVerb = nodeVerb.getNodeValue();

			if (valorVerb != null && !"".equals(valorVerb) && "update_delete".equalsIgnoreCase(valorVerb)) {

				// percorre os todos os nodes efetuando as operações necessárias
				NodeList nodeList = document.getElementsByTagName("*");
				for (int i = 0; i < nodeList.getLength(); i++) {
					Element element = (Element) nodeList.item(i);
//					nodeList = document.getElementsByTagName("metering-point");
					if (element.getNodeName().equals("metering-point")) {
						w_match_result = element.getAttribute("nr");// TODO: ver
																	// se
																	// precisar
																	// ou
																	// DELETAR
					}
					if (element.getAttribute("nr") != null && !element.getAttribute("nr").trim().equals("")) {
						w_serialNumber = element.getAttribute("nr");
					}
					if ("exchange".equals(element.getNodeName())) {
						if (element.getAttribute("type").equals("meter-dismantling")) {
							element.setAttribute("type", "meter-mounting");
						}
					}

					if ("status".equals(element.getNodeName())) {
						// remove 2 proximos nodes <status type="ok">
						if ("ok".equals(element.getAttribute("type"))) {
							document.removeChild(element);
						}
						// remove 2 proximos nodes '<status type="error">
						if ("error".equals(element.getAttribute("type"))) {
							document.removeChild(element);
						}

					}

					// REPLACE FIRST OCCURRENCE OF '_Inv' IN Ssource WITH ''.
					// TODO: Implementar

					if ("identification".equals(element.getNodeName())) {
						element.setAttribute("ident1", "U");
					}

				} // END LOOP FOR

				// <assignment ident1="Template_" ident2="" ident3="" ident4=""
				// ident5=""/>
				Element assignmentNode = (Element) document.getElementsByTagName("assignment").item(0);
				String[] split = assignmentNode.getAttribute("ident1").split("_");
				if (split.length > 0 && "Template".equals(split[0])) {
					String wModel = split[1];
//					select * from Zmodel_OBISC into table t_zmodel_OBISC where model eq w_model.
					List<String> dadosDaQuery = new ArrayList<String>();
					Element channelListNode = document.createElement("datachannel-list");
					for (int i = 0; i < dadosDaQuery.size(); i++) {
						String wIdent1 = null;
						String wCanal = dadosDaQuery.get(i) + "getObisCode()";
						if (dadosDaQuery.get(i) + "OBISC.getFlagRegister()" == null
								|| "".equals(dadosDaQuery.get(i) + "OBISC.getFlagRegister()")) {
							wIdent1 = w_serialNumber;
						} else {
							wIdent1 = wCanal.substring(2, 5);
						}
						// String[] new String[]{""};
//						String[] attValues;
						criarNo("datachannel", "", channelListNode, new String[] { "obis-id-code", "ident1" },
								new String[] { wCanal, wIdent1 });
					}
				}
				/////////////////////////////////

				nodeVerb.setNodeValue("update_add");

				String local = "/interf/gle/prov_ret/out/PROV_RET_" + w_serialNumber + "_"
						+ new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date()) + ".xml";
				OutputStream xmlOut = new FileOutputStream(new File(local));

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(document);
				StreamResult result = new StreamResult(xmlOut);
				transformer.transform(source, result);

			} else {

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new StreamTransformationException("Falha ao processar o mapping ", e);
		}
	}

	private Element criarNo(String nodeName, String nodeValue, Element parentNode, String[] attNames,
			String[] attValues) {
		Element elem = document.createElement(nodeName);
		parentNode.appendChild(elem);
		if (nodeValue != null) {
			Node dateTxtNode = (Node) document.createTextNode(nodeValue.trim());
			elem.appendChild(dateTxtNode);
		}
		if (attNames != null) {
			for (int i = 0; i < attNames.length; i++) {
				if (!attValues[i].equals("")) {
					elem.setAttribute(attNames[i], attValues[i].trim());
				}
			}
		}
		return elem;
	}

	/**
	 * Recupera valor de um node de uma determinada tag do XML.
	 * 
	 * @param nodeLst
	 *            Lista de nodes.
	 * @return Valor do primeiro node da lista especificada.
	 */
	private String getValorNode(NodeList nodeLst) {
		String valorNo = null;

		if (nodeLst.getLength() != 0) {
			Node txtNode = (Node) nodeLst.item(0).getFirstChild();
			if (txtNode != null) {
				valorNo = String.valueOf(txtNode);
			}
		}
		return (valorNo == null ? "" : valorNo);
	}

	private Element getElementByTagName(NodeList nodeList, String tagName) {
		Node node = null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			node = nodeList.item(0);
			if (tagName.equals(node.getNodeName())) {
				return (Element) node;
			}
		}

		return (Element) node;
	}

	private Node getElementByAttribute(String tagName) {
		Node node = null;

		return node;
	}
}
