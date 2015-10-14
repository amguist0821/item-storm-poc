package com.walmart.move.eventing.core;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Class will contain the required implementation that is needed in order to take a given 
 * String and convert the string to an Object which can be utilized within the eventing
 * framework. 
 * 
 * @author amguist
 *
 * @param <T>
 */
public final class StringToGson<T> {

	/*
	 * Class Member Variables
	 */
	private static final 	String	dateFormat = "dd MMM yyyy HH:mm:ss";			// Format required for JSON Conversion
	private final	Gson	gson	=	new GsonBuilder().setDateFormat(dateFormat).create();
	
	private final Type		type;
	
	/**
	 * Function is defined as being the default class constructor which is to
	 * be utilized in order to initialize the utility that will provide the ability
	 * to convert from a String to an Object based on being a JSON formatted 
	 * String.
	 */
	public StringToGson() {
		this.type = null;
	}
	
	/**
	 * Function is defined as being an overriding class constructor which is to be utilized in order to
	 * provide a given type which is to be utilized during the conversion from a String to 
	 * a JSON Object.
	 * 
	 * @param type
	 */
	public StringToGson(Type type) {
		this.type = type;
	}
	
	/**
	 * Function will be utilized in order to provide the ability to convert 
	 * a supplied Domain String to that of a given Object, thus allowing for the
	 * ability to then read the supplied Object within the Event 
	 * Framework.
	 * 
	 * @param domainString
	 * @return
	 */
	public T convert(String domainString) {
		return(this.gson.fromJson(domainString, this.type));
	}
}
