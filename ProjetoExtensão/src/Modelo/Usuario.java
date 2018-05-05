package Modelo;

import Util.Arquivo;
import java.util.ArrayList;
import glempoa.GLEMPOAArray;
import glempoa.GLEMPOAObject;

/**
 *
 * @author mathe
 */
public class Usuario {
    private String Matricula;
    private String Nome;

    public Usuario() {
    }

    public Usuario(String Matricula, String Nome) {
        this.Matricula = Matricula;
        this.Nome = Nome;
    }
    
    public Usuario(GLEMPOAObject json) {
        this.Matricula = json.getString("matricula");
        this.Nome = json.getString("nome");
    }
    
    public String getMatricula() {
        return Matricula;
    }

    public void setMatricula(String Matricula) {
        this.Matricula = Matricula;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String Nome) {
        this.Nome = Nome;
    }
    
    public GLEMPOAObject toJson(){
        GLEMPOAObject json = new GLEMPOAObject();
        json.put("nome",this.Nome);
        json.put("matricula",this.Matricula);
        return json;
    }
    
    public boolean Persistir(){
        GLEMPOAObject json = this.toJson();
        
        String base = Arquivo.Read();
        GLEMPOAArray jA = new GLEMPOAArray();
        if(!base.isEmpty() && base.length()>5)
            jA = new GLEMPOAArray(base);
        
        jA.put(json);
        Arquivo.Write(jA.toString());
       
        return true;
    }
    
    public static ArrayList<Usuario> getAlunos(){
        ArrayList<Usuario> alunos = new ArrayList();
        String base = Arquivo.Read();
        if(base.isEmpty() || base.length()<5)
            return null;
        
        GLEMPOAArray jA = new GLEMPOAArray(base);
        for(int i=0;i<jA.length();i++){
            Usuario A = new Usuario(jA.getGLEMPOAObject(i));
            alunos.add(A);
        }
        return alunos;
    }
    
}
