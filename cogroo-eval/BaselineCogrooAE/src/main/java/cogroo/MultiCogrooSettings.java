package cogroo;
/**
 * Copyright (C) 2008 CoGrOO Team (cogroo AT gmail DOT com)
 * 
 * CoGrOO Team (cogroo AT gmail DOT com)
 * LTA, PCS (Computer and Digital Systems Engineering Department),
 * Escola Politécnica da Universidade de São Paulo
 * Av. Prof. Luciano Gualberto, trav. 3, n. 380
 * CEP 05508-900 - São Paulo - SP - BRAZIL
 * 
 * http://cogroo.sourceforge.net/
 * 
 * This file is part of CoGrOO.
 * 
 * CoGrOO is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public as published by 
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CoGrOO is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with CoGrOO. If not, see <http://www.gnu.org/licenses/>.
 */



import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Class that helps to get configurations from {@link ResourceBundle} properties file
 * @author colen
 *
 */
public final class MultiCogrooSettings {
  
  private static final Properties PROPERTIES = new Properties();
  
  static {
    try {
      PROPERTIES.load(MultiCogrooSettings.class.getResourceAsStream("/cogroo/multi.properties"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

//    private static final ResourceBundle PROPERTIES = ResourceBundle.getBundle("cogroo.multi");

    private static final String KEY_SENT = "sent";
    public static final boolean SENT = getBoolean(KEY_SENT);
    
    private static final String KEY_TOK = "tok";
    public static final boolean TOK = getBoolean(KEY_TOK);
    
    private static final String KEY_PRE = "pre";
    public static final boolean PRE = getBoolean(KEY_PRE);
    
    // tagger
    
    private static final String KEY_CHUNKER = "chunker";
    public static final boolean CHUNKER = getBoolean(KEY_CHUNKER);

    
    private static final String KEY_SP = "sp";
    public static final boolean SP = getBoolean(KEY_SP);

    private static boolean getBoolean(String key) {
        return Boolean.parseBoolean(PROPERTIES.getProperty(key));
    }

    private MultiCogrooSettings() {
        // Prevents instantiation.
    }

}
