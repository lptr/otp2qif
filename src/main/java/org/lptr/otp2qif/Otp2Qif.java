package org.lptr.otp2qif;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Otp2Qif {
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void convert(File inputFile, File outputFile) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, ParseException {
		outputFile.getParentFile().mkdirs();
		outputFile.delete();
		Writer writer = Files.asCharSink(outputFile, Charset.forName("ISO-8859-2")).openStream();
		try {
			convert(inputFile, writer);
		} finally {
			writer.close();
		}
	}

	public static void convert(File inputFile, Writer writer) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, ParseException {
		String input = Files.asCharSource(inputFile, Charset.forName("ISO-8859-2")).read().replaceAll("&nbsp;", " ");
		String output = convert(input);
		writer.write(output);
	}

	public static String convert(String input) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, ParseException {
		StringBuilder result = new StringBuilder();
		result.append("!Type:Bank\n");

		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		documentBuilder.setEntityResolver(new EntityResolver() {
			@Override
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
				return new InputSource(new StringReader(""));
			}
		});
		Document document = documentBuilder.parse(new InputSource(new StringReader(input)));
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xPath = xPathFactory.newXPath();
		String accountNumber = xPath.evaluate("//td[text() = 'Számlaszám']/following-sibling::td/text()", document);
		NodeList rows = (NodeList) xPath.evaluate("//table[@class = 'eredmenytabla']/tbody/tr", document, XPathConstants.NODESET);
		SimpleDateFormat excelDateFormat = new SimpleDateFormat("yyyy.MM.dd.");
		SimpleDateFormat quickenDateFormat = new SimpleDateFormat("yyyy.MM.dd");
		for (int i = 0; i < rows.getLength(); i++) {
			Node row = (Node) rows.item(i);
			String transferType = xPath.evaluate("td[2]", row).trim();
			Date bookingDate = excelDateFormat.parse(xPath.evaluate("td[3]", row).trim());
			Date transactionDate = excelDateFormat.parse(xPath.evaluate("td[4]", row).trim());
			BigDecimal amount = new BigDecimal(xPath.evaluate("td[5]", row).trim());
			BigDecimal newBalance = new BigDecimal(xPath.evaluate("td[6]", row).trim());
			String otherAccount = xPath.evaluate("td[7]", row).trim();
			String otherName = xPath.evaluate("td[8]", row).trim();
			String comment = xPath.evaluate("td[9]", row).trim();

			result.append('D').append(quickenDateFormat.format(transactionDate)).append('\n');
			result.append('T').append(amount.toPlainString()).append('\n');
			result.append('P').append(otherName).append('\n');
			result.append('M').append(transferType);
			if (!Strings.isNullOrEmpty(otherAccount)) {
				result.append(' ').append(otherAccount);
			}
			result.append(' ').append(comment);
			result.append('\n');
			result.append("^\n");
		}
		return result.toString();
	}
}
