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

/**
 * Implemtação em Java do programa Abap Z_MDM_XI_ZFA_ODR 
 * 
 * @author Dorival Querino da Silva
 * 
 * - 7 de dez de 2016 - CSC
 * 
 */
public class ZMdmXiZfaOdr implements StreamTransformation {

	private Map map;
	private Document document;
	private String nodeName3;
	private String wNodeName;
	private String wNodeValue;
	private String wFieldName;
	private String wtableName;
	private Integer w_index;
	private String w_fieldname;
	private String w_tablename;
	private String w_node_name;
	private Node w_node;
	private String w_node_value;

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
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);

			NodeList emp_node_iterator = document.getElementsByTagName("MeterReadsRequestMessage");
			/* 1 */for (int i = 0; i < emp_node_iterator.getLength(); i++) {// primeiro nivel
				Node emp_node = emp_node_iterator.item(i);
				NodeList emp_node_list = emp_node.getChildNodes();
				/* 2 */for (int j = 0; j < emp_node_list.getLength(); j++) {// Percorre tags do primeiro nivel
					Node emp_subnode = emp_node_list.item(i);
					String emp_subnode_name = emp_subnode.getNodeName();
					NodeList emp_subnode_list = emp_node.getChildNodes();
					/* 3 */for (int k = 0; k < emp_subnode_list.getLength(); k++) {// Percorre tags do segundo nivel
						Node emp_subnode2 = emp_subnode_list.item(k);
						NodeList emp_subnode_list2 = emp_subnode2.getChildNodes();
						String emp_subnode_name2 = emp_subnode2.getNodeName();
						// w_index = sy-index - 1.
						w_node = emp_subnode_list2.item(w_index);
						if (emp_subnode_name2.equals("Meter")) {
							/* 4 */for (int l = 0; l < emp_subnode_list2.getLength(); l++) {// Percorre tags do Terceiro Nivel
								Node emp_subnode3 = emp_subnode_list2.item(l);
								String emp_subnode_name3 = emp_subnode3.getNodeName();
								NodeList emp_subnode_list3 = emp_subnode3.getChildNodes();
								// w_index = sy-index - 1.
								w_node_name = emp_subnode3.getNodeName();
								w_node_value = emp_subnode3.getNodeValue();
								w_fieldname = "fs_".concat("-").concat(w_node_name);
							}
							w_fieldname = "fs_" + emp_subnode_name2;
							// ASSIGN (w_fieldname) TO <fs>.
							w_tablename = "t_" + emp_subnode_name2;
							// ASSIGN (w_tablename) TO <fs_table>.
							// IF sy-subrc EQ 0.
							// APPEND <fs> TO <fs_table>.
							// END IF.
						} else {
							// w_index = sy-index - 1;
							w_node = emp_subnode_list.item(w_index);
							w_node_name = w_node.getNodeName();
							w_node_value = w_node.getNodeValue();
							w_fieldname = "fs_" + emp_subnode_name + "-" + w_node_name;
							// ASSIGN (w_fieldname) TO <fs>.
							// IF sy-subrc EQ 0.
							// MOVE w_node_value TO <fs>.
							// ENDIF.
						}
					}
					w_fieldname = "fs_" + emp_subnode_name;
					// ASSIGN (w_fieldname) TO <fs>.
					w_tablename = "t_" + emp_subnode_name;
					// ASSIGN (w_tablename) TO <fs_table>.
					// IF sy-subrc EQ 0.
					// APPEND <fs> TO <fs_table>.
					// ENDIF.

				}
			}
			//////////////////////////////////////////////
			{
				// odocument = ixml_factory->create_document( ).

			}
			//////////////////////////////////////////////
		} catch (Exception e) {
			e.printStackTrace();
			throw new StreamTransformationException("Falha ao processar o mapping ", e);
		}
	}
}
