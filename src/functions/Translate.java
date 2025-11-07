package functions;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import application.Shutter;
import application.Utils;
import library.FFMPEG;
import settings.FunctionUtils;

public class Translate extends Shutter {
	
    private static final int CHUNK_SIZE = 5000;

    public static void main() {

		Thread thread = new Thread(new Runnable(){
			
			@Override
			public void run() {
				
				lblFilesEnded.setText(FunctionUtils.completedFiles(FunctionUtils.completed));
				lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
				
				btnStart.setEnabled(false);
				btnCancel.setEnabled(true);				
				
				for (int i = 0 ; i < list.getSize() ; i++)
				{
					FFMPEG.error = false;
					progressBar1.setValue(0);
					
					File file = FunctionUtils.setInputFile(new File(list.getElementAt(i)));		
										
					if (file == null)
						break;

					try {
						
						//input filename
						String fileName = file.getName();
						final String extension =  fileName.substring(fileName.lastIndexOf("."));
						
						if (extension.equals(".txt") || extension.equals(".vtt") || extension.equals(".srt"))
						{
							lblCurrentEncoding.setText(fileName);		         
	
							//Output folder
							String labelOutput = FunctionUtils.setOutputDestination("", file);
							
							//File output name
							String prefix = "";	
							if (casePrefix.isSelected())
							{
								prefix = FunctionUtils.setPrefixSuffix(txtPrefix.getText(), false);
							}
							
							String extensionName = "";	
							if (btnExtension.isSelected())
							{
								extensionName = FunctionUtils.setPrefixSuffix(txtExtension.getText(), false);
							}
							
							//Output language
							String selectedLanguage = Utils.ISO_639_2_LANGUAGES[comboFilter.getSelectedIndex()][0];
							
							//Output name
							String fileOutputName =  labelOutput.replace("\\", "/") + "/" + prefix + fileName.replace(extension, extensionName + "_" + comboFilter.getSelectedItem() + extension); 
													
					        String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
					        List<String> chunks = chunkText(content, CHUNK_SIZE);
					    	progressBar1.setMaximum(chunks.size());    
	  	
							//Command
							String translated = translateText(content, "auto", selectedLanguage);
													
				            Files.writeString(new File(fileOutputName).toPath(), translated, StandardCharsets.UTF_8);
						}
						else
							FFMPEG.error = true;
				            
						if (FFMPEG.saveCode == false)
						{
							if (lastActions(file, fileName))
								break;
						}
					
					} catch (Exception e) {
						e.printStackTrace();
						FFMPEG.error = true;
					}
				}	
								
				endOfFunction();			
			}
			
		});
		thread.start();
    }

    private static String translateText(String text, String sourceLang, String targetLang) throws Exception {    	
    	List<String> chunks = chunkText(text, CHUNK_SIZE);
    	
        StringBuilder result = new StringBuilder();
        for (String chunk : chunks)
        {        	
            result.append(translateChunk(chunk, sourceLang, targetLang)).append(" ");
            
            progressBar1.setValue(progressBar1.getValue() + 1);
            
            if (cancelled)
            	break;
            
            //Thread.sleep(400);            
        }
        return result.toString().trim();
    }
    
    private static String translateChunk(String text, String sourceLang, String targetLang) throws Exception {
        String urlStr = String.format(
            "https://translate.googleapis.com/translate_a/single?client=gtx&sl=%s&tl=%s&dt=t&q=%s",
            URLEncoder.encode(sourceLang, "UTF-8"),
            URLEncoder.encode(targetLang, "UTF-8"),
            URLEncoder.encode(text, "UTF-8")
        );

        @SuppressWarnings("deprecation")
		URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)))
        {
            String response = reader.readLine();
            return parseTranslation(response);
        }
    }

    private static String parseTranslation(String json) {
        JSONArray arr = new JSONArray(json);
        JSONArray inner = arr.getJSONArray(0);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inner.length(); i++) {
            sb.append(inner.getJSONArray(i).getString(0));
        }
        return sb.toString();
    }

    private static List<String> chunkText(String text, int chunkSize) {
    	List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        int length = text.length();
        for (int i = 0; i < length; ) {
            // Find next whitespace or newline
            int nextSpace = text.indexOf(' ', i);
            int nextNewline = text.indexOf('\n', i);

            // Pick whichever comes first
            int next = -1;
            if (nextSpace == -1 && nextNewline == -1) {
                next = length;
            } else if (nextSpace == -1) {
                next = nextNewline;
            } else if (nextNewline == -1) {
                next = nextSpace;
            } else {
                next = Math.min(nextSpace, nextNewline);
            }

            String token = text.substring(i, Math.min(next, length));

            // Add the token
            if (current.length() + token.length() + 1 > chunkSize) {
                chunks.add(current.toString());
                current.setLength(0);
            }

            current.append(token);

            // Preserve newline tokens
            if (next == nextNewline) {
                current.append('\n');
            } else {
                current.append(' ');
            }

            i = next + 1;
        }

        if (current.length() > 0) {
            chunks.add(current.toString());
        }

        return chunks;
    }
    
    private static boolean lastActions(File file, String fileName) {	
		
		//Errors
		if (FFMPEG.error)
		{
			FFMPEG.errorList.append(fileName);
		    FFMPEG.errorList.append(System.lineSeparator());
		}

		//Process cancelled
		if (cancelled)
		{
			return true;
		}
		else
		{
			if (FFMPEG.error == false)
				FunctionUtils.completed++;
			
			lblFilesEnded.setText(FunctionUtils.completedFiles(FunctionUtils.completed));
		}
		
		//Sending process
		FunctionUtils.addFileForMail(fileName);		
		
		return false;
	}
}
