package piotrrr.thesis.bots.tuning;



public interface Config {
		
	public float getParameter(String name) throws Exception;
	
	public float getParameterMin(String name) throws Exception;
	
	public float getParameterMax(String name) throws Exception;
	
	public boolean isParameterInteger(String name) throws Exception;
		

}
