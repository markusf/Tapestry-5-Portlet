package de.wiv.tapestryportlet2.pages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.services.Response;

public class Contact
{
	public Object onActivate(){
		return new StreamResponse(){

			public String getContentType() {
				// TODO Auto-generated method stub
				return "application/zip";
			}

			public InputStream getStream() throws IOException {
				return new ByteArrayInputStream(new String("hellohello").getBytes());
			}

			public void prepareResponse(Response arg0) {
				// TODO Auto-generated method stub
				
			}
			
		};
	}
}
