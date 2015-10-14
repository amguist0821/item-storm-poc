package com.walmart.move.event.item.core;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class defines the contract that is to be utilized when it comes to processing
 * freight within any type of node that is part of the Walmart Supply Chain.
 * 
 * @author amguist
 *
 */
@XmlRootElement
public class Item {

	/*
	 * Class Member Variables
	 */
	private Long	walmartItemNumber;
	
	/**
	 * Function is defined as the default class constructor which is needed in order to 
	 * properly utilize JAX-B.
	 */
	public Item() {
		
	}
	
	/**
	 * Function provides the ability to return back to the calling method the
	 * Walmart Item Number that has been defined for a given Product.
	 * 
	 * @return Walmart Item Number
	 */
	public Long	getWalmartItemNumber() {
		return(this.walmartItemNumber);
	}
	
	/**
	 * Function provides the ability to override the current value of Walmart
	 * Item Number based on the Walmart Item Number that is supplied within
	 * the input parameters.
	 * 
	 * @param walmartItemNumber
	 */
	public void setWalmartItemNumber(Long walmartItemNumber) {
		this.walmartItemNumber = walmartItemNumber;
	}
}
