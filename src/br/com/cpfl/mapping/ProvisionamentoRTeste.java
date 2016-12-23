package br.com.cpfl.mapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import javax.xml.transform.OutputKeys;
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

public class ProvisionamentoRTeste implements StreamTransformation {
	private Map map;
//	private Document document;
	private Document docSaida;
	private String w_serialNumber = "S432";

	@Override
	public void setParameter(Map map) {
		this.map = map;
		if (map == null) {
			this.map = new HashMap();
		}
	}

	@Override
	public void execute(InputStream inputStream, OutputStream outputStream) throws StreamTransformationException {
		try {

			DocumentBuilderFactory builderFactorySaida = DocumentBuilderFactory.newInstance();
			DocumentBuilder builderSaida = builderFactorySaida.newDocumentBuilder();
			this.docSaida = builderSaida.newDocument();

			Element root = docSaida.createElement("rootElement");
			
			root.setAttribute("id", "001");
			docSaida.appendChild(root);
			
			Element filho = docSaida.createElement("datachannel-list");
			filho.setAttribute("id-list", "002");
			Node item = docSaida.getElementsByTagName("rootElement").item(0);
			item.appendChild(filho);
			
			MappingDataAccessRemote mappingService = getMappingService();
			String wModel = "modelA3557";
			String sql = " SELECT * FROM Z_MODEL_OBISC1 ";
			// List dadosDaQuery =
//			List dadosDaQuery = mappingService.executarQueryGenerica(sql, TmpZmodelObisc.class);
			List dadosDaQuery = mappingService.consultarModelo(wModel );

//			Element channelListNode = docSaida.createElement("datachannel-list");
			if (!dadosDaQuery.isEmpty()) {
			for (int i = 0; i < dadosDaQuery.size(); i++) {
				TmpZmodelObisc zmodel = (TmpZmodelObisc) dadosDaQuery.get(i);
				String wCanal = zmodel.getId().getObiscode();
				String wIdent1 = null;
				if (zmodel.get_flagRegister_() == null || "".equals(zmodel.get_flagRegister_())) {
					wIdent1 = w_serialNumber ;
				} else {
					wIdent1 = wCanal.substring(2, 5);
				}

				Element ch = docSaida.createElement("datachannel");
				ch.setAttribute("id-channel", wIdent1);
				docSaida.getElementsByTagName("datachannel-list").item(0).appendChild(ch);
				
				//Node item = docSaida.getElementsByTagName("rootElement").item(0);
				//channelListNode.appendChild(ch);
				
				Element novoNode = criarNo("datachannel", "", (Element)docSaida.getElementsByTagName("datachannel-list").item(0), new String[] { "obis-id-code", "ident1" },
						new String[] { wCanal, wIdent1 });

				
			}
			}
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "html");
			DOMSource source = new DOMSource(docSaida);
			OutputStream out2 = outputStream;
			StreamResult result = new StreamResult(outputStream);
			transformer.transform(source, result);
		} catch (Exception e) {
			throw new StreamTransformationException("Falha ao processar o mapping ", e);
		}
	}

	private Element criarNo(String nodeName, String nodeValue, Element parentNode, String[] attNames,
			String[] attValues) {
		Element elem = docSaida.createElement(nodeName);
		parentNode.appendChild(elem);
		if (nodeValue != null) {
			Node dateTxtNode = (Node) docSaida.createTextNode(nodeValue.trim());
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

	public static void main(String[] args) throws Exception {

		System.out.println("Inicio: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
		long timeInMillis = Calendar.getInstance().getTimeInMillis();

		ProvisionamentoRTeste zProv = new ProvisionamentoRTeste();

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


		} catch (NamingException e) {
			e.printStackTrace();
		}
		return bean;
	}

}
