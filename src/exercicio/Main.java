package exercicio;

import java.io.IOException;



public class Main {
	public static ManipuladorImagem teste = new ManipuladorImagem();
	
	public static void main(String[] args) throws IOException {
	
		teste.lerImagem("/Users/Mila/workspace/Aspectos_Diff/src/exercicio/lena.pgm");
		//teste.verificaPixelCentral();
		
		//int[][] filtro = new int[][] { { -1, -1, -1 }, { -1, 8, -1 },
			//	 { -1, -1, -1 } };// Filtro de passa alta, fator 9
		//int[][]	filtro2 = new int[][]{{1,2,1},{2,4,2},{1,2,1}};//Filtro de suavizacao, fator 16
		//int[][] filtro3 = new int[][]{{0,0,0},{1,1,1},{0,0,0}};
		//teste.filtragem(filtro2, 16);
		//teste.copy();
		//teste.alterarContraste(0.5);
		//teste.alterarBrilho(100);
		//teste.negativarImagem();
		//teste.equilizar();
		//teste.equalize();
		//teste.mediana(3);
		//teste.testeOtsu();
		//teste.binariza();
		teste.difusao();
	//	teste.dilatar(filtro3);
		teste.salvar();
		//teste.lerImagemIdeal("/Users/Babi/workspace/ExercícioAspectos/src/exercicio/comparativo/HW6.pgm");
		//teste.salvarIdeal();
		//teste.accuracy();
		//teste.recall();
		//teste.precisao();
		//teste.especificidade();
		
		
		
		}	 
}
