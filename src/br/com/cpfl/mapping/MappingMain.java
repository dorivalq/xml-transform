package br.com.cpfl.mapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MappingMain {

	// public static void main(String[] args) throws Exception {
	//
	// System.out.println("Inicio: " + new
	// SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
	// long timeInMillis = Calendar.getInstance().getTimeInMillis();
	// ZTrataFalhaDeParaItens reg = new ZTrataFalhaDeParaItens();
	//
	// InputStream inputStream = new FileInputStream(
	// new File("D:\\desenv\\CPFL\\Workspace\\trata-falha\\tf-itens.xml"));
	// OutputStream outputStream = new FileOutputStream(
	// new File("D:\\desenv\\CPFL\\Workspace\\trata-falha\\tf-itens-OUT.xml"));
	//
	// reg.execute(inputStream, outputStream);
	//
	// System.out.println("Fim: " + new
	// SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
	// long timeInMillis2 = Calendar.getInstance().getTimeInMillis();
	// System.out.println("Tempo gasto = " + (Double.valueOf(timeInMillis2 -
	// timeInMillis) / 1000) + " segundo(s)");
	// }

	// public static void main(String[] args) throws Exception {
	//
	// System.out.println("Inicio: " + new
	// SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
	// long timeInMillis = Calendar.getInstance().getTimeInMillis();
	// ZTrataFalhaRegistradorFaturado reg = new
	// ZTrataFalhaRegistradorFaturado();
	//
	// InputStream inputStream = new FileInputStream(
	// new
	// File("D:\\desenv\\CPFL\\Workspace\\trata-falha\\trata-falha-registrador.xml"));
	// OutputStream outputStream = new FileOutputStream(
	// new
	// File("D:\\desenv\\CPFL\\Workspace\\trata-falha\\trata-falha-registrador-OUT.xml"));
	//
	// reg.execute(inputStream, outputStream);
	//
	// System.out.println("Fim: " + new
	// SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
	// long timeInMillis2 = Calendar.getInstance().getTimeInMillis();
	// System.out.println("Tempo gasto = " + (Double.valueOf(timeInMillis2 -
	// timeInMillis) / 1000) + " segundo(s)");
	// }

//	public static void main(String[] args) throws Exception {
//
//		System.out.println("Inicio: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
//		long timeInMillis = Calendar.getInstance().getTimeInMillis();
//
//		ZTrataFalhaDePara ztf = new ZTrataFalhaDePara();
//
//		InputStream inputStream = new FileInputStream(
//				new File("D:\\desenv\\CPFL\\Workspace\\trata-falha\\instanceTrataFalha.xml"));
//		OutputStream outputStream = new FileOutputStream(
//				new File("D:\\desenv\\CPFL\\Workspace\\trata-falha\\instanceTrataFalhaOUT.xml"));
//
//		ztf.execute(inputStream, outputStream);
//
//		System.out.println("Fim: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
//		long timeInMillis2 = Calendar.getInstance().getTimeInMillis();
//		System.out.println("Tempo gasto = " + (Double.valueOf(timeInMillis2 - timeInMillis) / 1000) + " segundo(s)");
//
//	}

//	public static void main(String[] args) throws Exception {
//
//		System.out.println("Inicio: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
//		long timeInMillis = Calendar.getInstance().getTimeInMillis();
//
//		ZTrataFalhaRegistradorFaturadoO ztf = new ZTrataFalhaRegistradorFaturadoO();
//
//		InputStream inputStream = new FileInputStream(
//				new File("D:\\desenv\\CPFL\\Workspace\\trata-falha\\tf-registrador-o.xml"));
//		OutputStream outputStream = new FileOutputStream(
//				new File("D:\\desenv\\CPFL\\Workspace\\trata-falha\\tf-registrador-o-OUT.xml"));
//
//		ztf.execute(inputStream, outputStream);
//
//		System.out.println("Fim: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
//		long timeInMillis2 = Calendar.getInstance().getTimeInMillis();
//		System.out.println("Tempo gasto = " + (Double.valueOf(timeInMillis2 - timeInMillis) / 1000) + " segundo(s)");
//
//	}

	public static void main(String[] args) throws Exception {

		System.out.println("Inicio: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
		long timeInMillis = Calendar.getInstance().getTimeInMillis();

		//ProvisionamentoRTeste zProv = new ProvisionamentoRTeste();
		ZZfaXiMdmProvisionamentoR zProv = new ZZfaXiMdmProvisionamentoR();
		
		InputStream inputStream = new FileInputStream(
				new File("D:\\desenv\\CPFL\\Workspace\\trata-falha\\dados\\provisionamento-r-UniversalAMIInterface2.xml"));
		OutputStream outputStream = new FileOutputStream(
				new File("D:\\desenv\\CPFL\\Workspace\\trata-falha\\dados\\provisionamento-r-UniversalAMIInterface2-OUT.xml"));

		zProv.execute(inputStream, outputStream);

		System.out.println("Fim: " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
		long timeInMillis2 = Calendar.getInstance().getTimeInMillis();
		System.out.println("Tempo gasto = " + (Double.valueOf(timeInMillis2 - timeInMillis) / 1000) + " segundo(s)");

	}

}
