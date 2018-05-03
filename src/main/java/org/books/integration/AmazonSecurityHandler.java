package org.books.integration;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class AmazonSecurityHandler implements SOAPHandler<SOAPMessageContext> {

	private static final String ACCESS_KEY = "AKIAIYW53DPUAJDHKOVQ";
	private static final String SECRET_KEY = "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";
	private static final String SECURITY_NAMESPACE = "http://security.amazonaws.com/doc/2007-01-01/";
	private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	private static final String MAC_ALGORITHM = "HmacSHA256";
	private Mac mac;

	public AmazonSecurityHandler() {
		try {
			SecretKey key = new SecretKeySpec(SECRET_KEY.getBytes(), MAC_ALGORITHM);
			mac = Mac.getInstance(MAC_ALGORITHM);
			mac.init(key);
		} catch (NoSuchAlgorithmException | InvalidKeyException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (outbound) {
			try {
				addSecurityHeader(context.getMessage());
			} catch (SOAPException ex) {
				throw new RuntimeException(ex);
			}
		}
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	@Override
	public void close(MessageContext context) {
	}

	private void addSecurityHeader(SOAPMessage request) throws SOAPException {
		String operation = request.getSOAPBody().getFirstChild().getLocalName();
		DateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
		String timestamp = dateFormat.format(Calendar.getInstance().getTime());
		byte[] data = mac.doFinal((operation + timestamp).getBytes());
		String signature = DatatypeConverter.printBase64Binary(data);

		SOAPHeader header = request.getSOAPHeader();
		if (header == null) {
			SOAPEnvelope envelope = request.getSOAPPart().getEnvelope();
			envelope.addHeader();
			header = envelope.getHeader();
		}
		header.addNamespaceDeclaration("aws", SECURITY_NAMESPACE);
		header.addChildElement("AWSAccessKeyId", "aws").addTextNode(ACCESS_KEY);
		header.addChildElement("Timestamp", "aws").addTextNode(timestamp);
		header.addChildElement("Signature", "aws").addTextNode(signature);
	}
}
