package teste.sax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxParserTest extends DefaultHandler {

	public static void main(String[] args) {

		new SaxParserTest().testPrser();
	}

	private String temp;
	private Account acct;
	private List<Account> list = new ArrayList<Account>();

	private void testPrser() {
		try {
			SAXParserFactory fact = SAXParserFactory.newInstance();
			SAXParser sp = fact.newSAXParser();

			SaxParserTest handler = new SaxParserTest();

			sp.parse("bank.xml", handler);

			handler.readList();

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		this.temp = new String(ch, start, length);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		this.temp = "";
		if (qName.equalsIgnoreCase("Account")) {
			acct = new Account();
			acct.setType(attributes.getValue("type"));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("Account")) {
			list.add(acct);
		} else if (qName.equalsIgnoreCase("id")) {	
			acct.setId(Integer.parseInt(temp));
		}else if (qName.equalsIgnoreCase("name")) {
			acct.setName(temp);
		}else if (qName.equalsIgnoreCase("amt")) {
			acct.setAmt(Integer.parseInt(temp));
		}
	}

	private void readList() {
		System.out.println("Nuber of acconts in bank: " + list.size());
		Iterator<Account> it = list.iterator();
		while (it.hasNext()) {
			System.out.println(it.next().toString());
		}
	}
}
