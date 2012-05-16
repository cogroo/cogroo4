package br.ccsl.cogroo.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class LanguageConfigurationUtil {

  public static LanguageConfiguration get(Locale locale) throws FileNotFoundException, JAXBException {
    File file = new File("src/main/schema/example.xml");
    FileInputStream in = new FileInputStream(file);
    
    return unmarshal(in);
  }


  private static LanguageConfiguration unmarshal(InputStream inputStream)
      throws JAXBException {
    String packageName = LanguageConfiguration.class.getPackage().getName();
    JAXBContext jc = JAXBContext.newInstance(packageName);
    Unmarshaller u = jc.createUnmarshaller();
    return (LanguageConfiguration)  u.unmarshal(inputStream);
    //return doc.getValue();
  }
  
  public static void main(String[] args) throws FileNotFoundException, JAXBException {
    LanguageConfiguration lc = LanguageConfigurationUtil.get(null);
    
    System.out.println(lc.getLocale());
    
    System.out.println(lc.getModels().getModel().get(0).getType());
    System.out.println(lc.getModels().getModel().get(0).getValue());
  }

}
