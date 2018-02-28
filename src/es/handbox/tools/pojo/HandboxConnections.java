package es.handbox.tools.pojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class HandboxConnections {
    protected static String HOSTv2;// = "db.handbox.es";
    protected static String PORTv2;// = "3306";
    protected static String USERv2;// = "isra_hbox";
    protected static String PWDv2;// = "D13g0&Iv4n";
    protected static String DATABASEv2;// = "Handbox_Master_DB";
    protected static String PREFIJOV2;// = "wp_";
    protected static String UPLOADSDIRv2; ///usr/share/nginx/www/wp-content/uploads/
    protected static String RSYNCv2;   //feeds o root

    protected static String HOSTv1;// = "handbox.es";
    protected static String PORTv1;// = "3306";
    protected static String USERv1;// = "lanoa";
    protected static String PWDv1;// = "m3l4p3l4!";
    protected static String DATABASEv1;// = "Handbox_prod_db";
    protected static String PREFIJOV1;// = "handbox_";
    protected static String UPLOADSDIRv1; ///var/www/wordpress/wp-content/uploads/
    
    protected static ArrayList<String> palabrasProhibidas;
    protected static ArrayList<String> palabrasNoCategorias;

    protected String uriv1;
    protected String uriv2;
    
    Connection connectionv1;

    Connection connectionv2;
    public HandboxConnections(String sentido) {
        Properties prop = new Properties();
                InputStream input = null;
         
                try {
                    //String propertiesPath = System.getProperty("handbox.properties.path", "D:\\PROYECTOS\\LAB\\hbx\\HbxTools\\public_html\\configuration");
                    //propertiesPath += "\\handboxtools_"+sentido+".properties";
                    String propertiesPath = System.getProperty("handbox.properties.path", "/home/israelg/config");
                    propertiesPath += "/handboxtools_"+sentido+".properties";
                        //input = new FileInputStream("D:\\temp\\handboxtools.properties");

                        input = new FileInputStream(propertiesPath);
         
                        // load a properties file
                        prop.load(input);
         
                        // get the property value and print it out
                        prop.getProperty("");
                        HOSTv2 = prop.getProperty("HOSTv2");//"db.handbox.es";
                        PORTv2 = prop.getProperty("PORTv2");//"3306";
                        USERv2 = prop.getProperty("USERv2");//"isra_hbox";
                        PWDv2 = prop.getProperty("PWDv2");//"D13g0&Iv4n";
                        DATABASEv2 = prop.getProperty("DATABASEv2");//"Handbox_Master_DB";
                        PREFIJOV2 = prop.getProperty("PREFIJOV2");//"Handbox_Master_DB";
                        UPLOADSDIRv2 = prop.getProperty("UPLOADSDIRv2");
                        RSYNCv2 = prop.getProperty("RSYNCv2","root");
                    
                        HOSTv1 = prop.getProperty("HOSTv1");//"handbox.es";
                        PORTv1 = prop.getProperty("PORTv1");//"3306";
                        USERv1 = prop.getProperty("USERv1");//"lanoa";
                        PWDv1 = prop.getProperty("PWDv1");//"m3l4p3l4!";
                        DATABASEv1 = prop.getProperty("DATABASEv1");//"Handbox_prod_db";
                        PREFIJOV1 = prop.getProperty("PREFIJOV1");  
                        UPLOADSDIRv1 = prop.getProperty("UPLOADSDIRv1");
                            
                            
                        uriv1 = "jdbc:mysql://" + HOSTv1 + ":" + PORTv1 + "/" + DATABASEv1;
                        uriv2 = "jdbc:mysql://" + HOSTv2 + ":" + PORTv2 + "/" + DATABASEv2;
                    
                    
                        palabrasProhibidas =  new ArrayList<String>(Arrays.asList(prop.getProperty("PALABRASPROHIBIDAS").split(",")));
         
                    palabrasNoCategorias =  new ArrayList<String>(Arrays.asList(prop.getProperty("PALABRASNOCATEGORIA").split(",")));
         
                } catch (IOException ex) {
                        ex.printStackTrace();
                } finally {
                        if (input != null) {
                                try {
                                        input.close();
                                } catch (IOException e) {
                                        e.printStackTrace();
                                }
                        }
                }
        try {
            connectionv1 = DriverManager.getConnection(uriv1, USERv1, PWDv1);
            connectionv2 = DriverManager.getConnection(uriv2, USERv2, PWDv2);
        } catch (SQLException sqle) {
            // TODO: Add catch code
            sqle.printStackTrace();
        }
    }
    protected void insertV2(String sentencia) {
        try {
            Statement sv2 = connectionv2.createStatement();
            sv2.execute(sentencia);
            sv2.close();
        } catch (SQLException sqle) {
            //System.out.println(sqle.toString());
        }
    }
    
    protected void insertV1(String sentencia) {
        try {
            Statement sv1 = connectionv1.createStatement();
            sv1.execute(sentencia);
            sv1.close();
        } catch (SQLException sqle) {
           //System.out.println(sqle.toString());
        }
    }

    protected ResultSet selectV1(String sentencia) {
        try {
            Statement sv1 = connectionv1.createStatement();
            ResultSet rs = sv1.executeQuery(sentencia);
            //sv1.close();
            return rs;
        } catch (SQLException sqle) {
            //System.out.println(sqle.toString());
        }
        return null;
    }

    protected ResultSet selectV2(String sentencia) {
        try {
            Statement sv2 = connectionv2.createStatement();
            ResultSet rs = sv2.executeQuery(sentencia);
            //sv2.close();
            return rs;
        } catch (SQLException sqle) {
           System.out.println(sqle.toString());
        }
        return null;
    }
}
