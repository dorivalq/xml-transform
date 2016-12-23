package br.com.cpfl.mapping;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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

import br.com.cpfl.daoejb4.MappingDataAccessRemote;
import br.com.cpfl.daoejb4.entidades.TmpZmodelObisc;

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
			Node nodeVerb = (Element) verbList.item(0);
			String valorVerb = nodeVerb.getTextContent();

			if (valorVerb != null && !"".equals(valorVerb) && "update_delete".equalsIgnoreCase(valorVerb)) {

				// percorre os todos os nodes efetuando as operações necessárias
				NodeList nodeList = document.getElementsByTagName("*");
				for (int i = 0; i < nodeList.getLength(); i++) {
					Element element = (Element) nodeList.item(i);
//					nodeList = document.getElementsByTagName("metering-point");
					if (element.getNodeName().equals("metering-point")) {
						w_match_result = element.getAttribute("nr");// TODO: ver
																	// se vai
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

				String[] split = null;
				if (assignmentNode != null && assignmentNode.getAttribute("ident1") != null) {
					split = assignmentNode.getAttribute("ident1").split("_");
					if (split != null && split.length > 0 && "Template".equals(split[0])) {
						String wModel = split[1];
//					select * from Zmodel_OBISC into table t_zmodel_OBISC where model eq w_model.
						MappingDataAccessRemote mappingService = getMappingService();
						String sql = " SELECT * FROM Z_MODEL_OBISC1 ";
						// List dadosDaQuery =
						// mappingService.consultarModelo(wModel);
						List dadosDaQuery = mappingService.executarQueryGenerica(sql);
//					List<String> dadosDaQuery = new ArrayList<String>();
						Element channelListNode = document.createElement("datachannel-list");
						document.getElementsByTagName("tns:header").item(0).appendChild(channelListNode);
						if (!dadosDaQuery.isEmpty()) {
							for (int i = 0; i < dadosDaQuery.size(); i++) {
								TmpZmodelObisc zmodel = (TmpZmodelObisc) dadosDaQuery.get(i);
								String wIdent1 = null;
								String wCanal = zmodel.getId().getObiscode();
								if (zmodel.get_flagRegister_() == null || "".equals(zmodel.get_flagRegister_())) {
									wIdent1 = w_serialNumber;
								} else {
									wIdent1 = wCanal.substring(2, 5);
								}

								criarNo("datachannel", "",
										(Element) document.getElementsByTagName("datachannel-list").item(0),
										new String[] { "obis-id-code", "ident1" }, new String[] { wCanal, wIdent1 });
							}
						}
					}
				}

				/////////////////////////////////

				nodeVerb.setNodeValue("update_add");

//				String local = "/interf/gle/prov_ret/out/PROV_RET_" + w_serialNumber + "_"
//						+ new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date()) + ".xml";
//				OutputStream xmlOut = new FileOutputStream(new File(local));

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
//				transformer.setOutputProperty(OutputKeys.METHOD, "html");
				OutputStream out2 = outputStream;
				DOMSource source = new DOMSource(document);
//				StreamResult result = new StreamResult(xmlOut);

				StreamResult result = new StreamResult(outputStream);
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

	public static void main(String[] args) throws Exception {

		System.out.println("Inicio: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
		long timeInMillis = Calendar.getInstance().getTimeInMillis();

//		ProvisionamentoRTeste zProv = new ProvisionamentoRTeste();
		ZZfaXiMdmProvisionamentoR zProv = new ZZfaXiMdmProvisionamentoR();

		InputStream inputStream = new FileInputStream(new File(
				"D:\\desenv\\CPFL\\Workspace\\trata-falha\\dados\\provisionamento-r-UniversalAMIInterface2.xml"));
		OutputStream outputStream = new FileOutputStream(new File(
				"D:\\desenv\\CPFL\\Workspace\\trata-falha\\dados\\provisionamento-r-UniversalAMIInterface2-OUT.xml"));

		zProv.execute(inputStream, outputStream);
	
		System.out.println("Fim: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
		long timeInMillis2 = Calendar.getInstance().getTimeInMillis();
		System.out.println("Tempo gasto = " + (Double.valueOf(timeInMillis2 - timeInMillis) / 1000) + " segundo(s)");

	}

	/*
	 * location of JBoss JNDI Service provider the client will use. It should be
	 * URL string.
	 */
	private static final String PROVIDER_URL = "jnp://localhost:1099";

	/*
	 * specifying the list of package prefixes to use when loading in URL
	 * context factories. colon separated
	 */
	private static final String JNP_INTERFACES = "org.jboss.naming:org.jnp.interfaces";

	/*
	 * Factory that creates initial context objects. fully qualified class name.
	 */
	private static final String INITIAL_CONTEXT_FACTORY = "org.jnp.interfaces.NamingContextFactory";

	private static Context context;

	public MappingDataAccessRemote getMappingService() {

		MappingDataAccessRemote bean = null;
		// Properties extends HashTable
		Properties prop = new Properties();
		prop.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
		prop.put(Context.URL_PKG_PREFIXES, JNP_INTERFACES);
		prop.put(Context.PROVIDER_URL, PROVIDER_URL);
		try {
			context = new InitialContext(prop);
			bean = (MappingDataAccessRemote) context.lookup("MappingDataAccessImpl/remote");

			TmpZmodelObisc model = (TmpZmodelObisc) bean.consultarModelo("123").get(0);
			System.out.println("Modelo recuperado de ejb remoto : " + model.getId().getModel());

		} catch (NamingException e) {
			e.printStackTrace();
		}
		return bean;
	}

}
