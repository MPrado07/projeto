package Controle;


// Essa classe é responsável pelo cadastro inicial dos usuários...


import Modelo.Usuario;
import java.util.ArrayList;

/**
 * @author mathe
 */
public class ControleDeUsuarios {
	
    public static boolean SalvarAluno(String Matricula,String Nome){
        Usuario a = new Usuario(Matricula, Nome); 					// Menu inicial, salva o cadastro do usuario (aluno)
        return a.Persistir();
    
    }
    
    public static ArrayList<String[]> getAlunos(){
        ArrayList<String[]> Alunos = new ArrayList();  //  vetor de armazenaments
        
        ArrayList<Usuario> A = Usuario.getAlunos(); 
        if(A!=null){
            for(int i=0;i<A.size();i++){
                String a[] = new String[2];
                a[0] = A.get(i).getMatricula();
                a[1] = A.get(i).getNome();
                Alunos.add(a);
            }
        }
        
        return Alunos;
    }
}
