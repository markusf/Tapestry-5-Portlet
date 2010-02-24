package de.wiv.tapestryportlet2.pages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.services.Response;

/**
 * Start page of application tapestryportlet2.
 */
public class Index
{
	public Date getCurrentTime() 
	{ 
		return new Date(); 
	}
	
	public StreamResponse onActionFromDownloadResource(){
		System.out.println("fromdownload");
		return new StreamResponse(){

			public String getContentType() {
				// TODO Auto-generated method stub
				return "application/schmock";
			}

			public InputStream getStream() throws IOException {
				return new ByteArrayInputStream(new String("hellohello").getBytes());
			}

			public void prepareResponse(Response arg0) {
				// TODO Auto-generated method stub
				
			}
			
		};
	}
	
	public Object onActionFromReDownloadResource(){
		System.out.println("fromredownload");
		return Contact.class;
	}
}
