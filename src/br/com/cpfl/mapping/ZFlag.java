package br.com.cpfl.mapping;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.sap.aii.mapping.api.StreamTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;

import br.com.cpfl.daoejb4.MappingDataAccessRemote;

public class ZFlag implements StreamTransformation {

	private Map map;

	@Override
	public void setParameter(Map map) {
		this.map = map;
		if (map == null) {
			this.map = new HashMap();
		}
	}

	@Override
	public void execute(InputStream inputStream, OutputStream outputStream) throws StreamTransformationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		Map<String, String> flagMap = new HashMap<String, String>();
		
		String sql = "SELECT FLAG_ZFA, FLAG_MDM FROM Z_FLAGS WHERE ";
		List listFlags = getMappingService().executarQueryGenerica(sql);
		for (int i = 0; i < listFlags.size(); i++) {
			flagMap.put(listFlags.get(i)+"getFlagZfa()", listFlags.get(i)+"getFlagMdm()");
		}
		
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(inputStream);
			NodeList nodeList = document.getElementsByTagName("ns0:flags");
			for (int i = 0; i < nodeList.getLength(); i++) {
				String nodeValue = nodeList.item(i).getNodeValue();
				
				//TODO: TESTAR SCANNER
				if (new Scanner(nodeValue).findInLine("[d]").isEmpty()) {
					long wSoma = 0;
					if (nodeValue.length() > 1) {

					} else {
						char[] arrayNodeValue = nodeValue.toCharArray();
						for (int j = 0; j < arrayNodeValue.length; j++) {

							if (flagMap.containsKey(arrayNodeValue[i])) {
								long base = Long.valueOf(flagMap.get(arrayNodeValue[i])) - 1;
								Long wBin = (long) Math.pow(2, base);// 2 base;
								wSoma = wSoma + wBin;
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
