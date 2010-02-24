package de.wiv.tapestryportlet2.pages;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;

public class About
{
	@InjectComponent
	private Form testForm;
	
	@Inject
	private Block sample;
	
	public void onValidateFormFromTestForm(){
		testForm.recordError("Hope it works!");
	}
	
	public void onSuccessFromTestForm(){
		System.out.println("success!");
	}
	
	public String getData(){
		return "bla";
	}
	
	public void setData(String data){
		System.out.println("setData");
	}
	
	public Object onActionFromShowdown(){
		return sample;
	}
	public Object onActionFromShowdownAgain(){
		return sample;
	}
}
