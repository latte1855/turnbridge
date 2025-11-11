/**
 * 
 */
package com.asynctide.turnbridge.service.parse;

/**
 * 
 */
public record InvoiceRecord(
	    int lineNo,
	    String invoiceNo,
	    String buyerId,
	    Double amount
	) {}
