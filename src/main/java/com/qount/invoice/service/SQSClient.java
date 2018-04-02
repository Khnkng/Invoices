package com.qount.invoice.service;

import java.util.List;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteMessageResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

/**
 * 
 * @author Ravi
 *
 */
public class SQSClient {

	private static final Logger LOGGER = Logger.getLogger(SQSClient.class);
	
	private static final String queURL = ConfigService.getInstance().getValue("notification_sqs_url");

	private static final AmazonSQSAsync sqs = createClient();

	/**
	 * 
	 * @return
	 */
	public static List<Message> longPoll() {
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest().withQueueUrl(queURL).withWaitTimeSeconds(20);
		List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
		return messages;

	}

	/**
	 * 
	 * @return
	 */
	private static AmazonSQSAsync createClient() {
		LOGGER.info("Creating SQS client");
		AmazonSQSAsync sqs = null;
		try {
			AWSStaticCredentialsProvider credentialsProvider = AwsManager.getCredentialsProvider();
			sqs = AmazonSQSAsyncClientBuilder.standard().withCredentials(credentialsProvider).withRegion(Regions.US_EAST_1).build();
			LOGGER.info("sqs client [ " + sqs.toString());
		} catch (Exception e) {
			LOGGER.error("Error creating SQS client",e);
		}
		return sqs;
	}
	
	/**
	 * 
	 * @param receiptHandle
	 */
	public static void deleteMessage(String receiptHandle){
		try {
			DeleteMessageResult result = sqs.deleteMessage(new DeleteMessageRequest(queURL, receiptHandle));
			LOGGER.debug("delete message result [ " + result.toString() + " ]");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error deleting message",e);
		}
		
	}
	
	public static void sendMessageAsync(String message) {
		new Thread(new Runnable() {
			public void run() {
				try {
					SendMessageRequest sendMessageRequest = new SendMessageRequest(queURL, message);
					sendMessageRequest.setMessageGroupId("notification");
					Future<SendMessageResult> result = sqs.sendMessageAsync(sendMessageRequest);
					SendMessageResult sendMessageResult = result.get();
					String sequenceNumber = sendMessageResult.getSequenceNumber();
					String messageId = sendMessageResult.getMessageId();
					LOGGER.debug("SendMessage succeed with messageId " + messageId + ", sequence number " + sequenceNumber + "\n");
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.error("Error sending message", e);
				}
			}
		}).start();
	}

	
	public static void main(String[] args) {
		List<Message> messages = SQSClient.longPoll();
		 for (Message message : messages) {
             System.out.println("  Message");
             System.out.println("    MessageId:     " + message.getMessageId());
             System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
             System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
             System.out.println("    Body:          " + message.getBody());
             SQSClient.deleteMessage(message.getReceiptHandle());
		 }
	}

}
